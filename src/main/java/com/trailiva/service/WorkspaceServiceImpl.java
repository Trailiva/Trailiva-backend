package com.trailiva.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.*;
import com.trailiva.data.repository.*;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.trailiva.data.model.TokenType.WORKSPACE_REQUEST;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    private final ModelMapper modelMapper;
    private final PersonalWorkspaceRepository personalWorkspaceRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OfficialWorkspaceRepository officialWorkspaceRepository;
    private final WorkspaceRequestTokenRepository tokenRepository;

    public WorkspaceServiceImpl(
            ModelMapper modelMapper,
            PersonalWorkspaceRepository personalWorkspaceRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            OfficialWorkspaceRepository officialWorkspaceRepository,
            WorkspaceRequestTokenRepository tokenRepository) {
        this.modelMapper = modelMapper;
        this.personalWorkspaceRepository = personalWorkspaceRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.officialWorkspaceRepository = officialWorkspaceRepository;
        this.tokenRepository = tokenRepository;
    }


    @Override
    public WorkSpace createWorkspace(WorkspaceRequest request, Long userId) throws WorkspaceException, UserException {
        User user = getAUserByUserId(userId);
        WorkSpace createdWorkspace = null;
        if (existByName(request.getName()))
            throw new WorkspaceException("Workspace with name already exist");

        if (WorkSpaceType.PERSONAL == request.getWorkSpaceType())
            createdWorkspace = createPersonalWorkspace(request, user);

        else if (WorkSpaceType.OFFICIAL == request.getWorkSpaceType())
            createdWorkspace = createOfficialWorkspace(request, user);

        return createdWorkspace;
    }

    @Override
    public void addMemberToOfficialWorkspace(List<String> memberEmails, Long userId) throws UserException {
        if (!memberEmails.isEmpty()) {
            for (String email : memberEmails) {
                sendWorkspaceRequestToken(userId, email);
            }
        }

    }

    @Override
    public void addModeratorToOfficialWorkspace(List<String> moderatorEmail, Long userId) throws UserException {
        if (!moderatorEmail.isEmpty()) {
            for (String email : moderatorEmail) {
                sendWorkspaceRequestToken(userId, email);
            }
        }
    }


    @Override
    public void addMemberToWorkspaceFromCSV(MultipartFile file, Long userId) throws IOException,
            CsvValidationException, UserException {
        CSVReader reader = new CSVReader(new FileReader(convertMultiPartToFile(file)));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            for (String email : nextLine) {
                sendWorkspaceRequestToken(userId, email);
            }
        }
    }

    private void sendWorkspaceRequestToken(Long userId, String email) throws UserException {
        User user = getAUserByEmail(email);
        String token = UUID.randomUUID().toString();
        User workspaceOwner = getAUserByUserId(userId);
        WorkspaceRequestToken requestToken = new WorkspaceRequestToken(token, user, WORKSPACE_REQUEST.toString(),
                workspaceOwner);
        tokenRepository.save(requestToken);
    }

    @Override
    public void addMemberToWorkspace(String requestToken) throws TokenException, UserException {
        WorkspaceRequestToken token = getToken(requestToken, WORKSPACE_REQUEST.toString());
        if (isValidToken(token.getExpiryDate())) throw new TokenException("Token has expired");
        onboardMember(token.getWorkspaceOwner(), token.getUser());
        tokenRepository.delete(token);
    }

    @Override
    public void addModeratorToWorkspace(String requestToken) throws TokenException, UserException {
        WorkspaceRequestToken token = getToken(requestToken, WORKSPACE_REQUEST.toString());
        if (isValidToken(token.getExpiryDate())) throw new TokenException("Token has expired");
        onboardModerator(token.getWorkspaceOwner(), token.getUser());
        tokenRepository.delete(token);
    }

    private WorkspaceRequestToken getToken(String token, String tokenType) throws TokenException {
        return tokenRepository.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new TokenException("Invalid token"));
    }

    @Override
    public OfficialWorkspace getOfficialWorkspace(Long workspaceId) throws WorkspaceException {
        return officialWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public OfficialWorkspace getUserOfficialWorkspace(Long userId) throws WorkspaceException {
        return officialWorkspaceRepository.findById(userId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public WorkSpace getUserPersonalWorkspace(Long userId) throws UserException {
        User user = getAUserByUserId(userId);
        return user.getPersonalWorkspace();
    }


    @Override
    public void addModeratorToWorkspaceFromCSV(MultipartFile file, Long userId) throws IOException,
            CsvValidationException, UserException {
        CSVReader reader = new CSVReader(new FileReader(convertMultiPartToFile(file)));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            for (String email : nextLine) {
                sendWorkspaceRequestToken(userId, email);
            }
        }
    }


    @Override
    public WorkSpace getPersonalWorkspace(Long workspaceId) throws WorkspaceException {
        return personalWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }


    private void onboardMember(User workspaceOwner, User member) throws UserException {
        if (userAlreadyExistInWorkspace(workspaceOwner.getOfficialWorkspace().getMembers(),
                workspaceOwner.getOfficialWorkspace().getModerators(), member.getEmail(), workspaceOwner.getEmail())) {
            throw new UserException("Member with email " + member.getEmail() + " already added to this workspace");
        }
        member.addMember(member);
        userRepository.save(workspaceOwner);
    }

    private void onboardModerator(User workspaceOwner, User member) throws UserException {
        if (userAlreadyExistInWorkspace(workspaceOwner.getOfficialWorkspace().getMembers(),
                workspaceOwner.getOfficialWorkspace().getModerators(), member.getEmail(), workspaceOwner.getEmail())) {
            throw new UserException("Moderator with email " + member.getEmail() + " already added to this workspace");
        }
        member.getRoles().add(roleRepository.findByName("ROLE_MODERATOR").get());
        workspaceOwner.addModerator(member);
        userRepository.save(workspaceOwner);
    }


    private WorkSpace createPersonalWorkspace(WorkspaceRequest request, User user) {
        PersonalWorkspace workSpace = modelMapper.map(request, PersonalWorkspace.class);
        PersonalWorkspace space = savePersonalWorkspace(workSpace);
        user.setPersonalWorkspace(space);
        userRepository.save(user);
        return space;
    }

    private WorkSpace createOfficialWorkspace(WorkspaceRequest request, User user) {
        user.getRoles().add(roleRepository.findByName("ROLE_SUPER_MODERATOR").get());
        OfficialWorkspace workSpace = modelMapper.map(request, OfficialWorkspace.class);
        OfficialWorkspace officialWorkspace = saveOfficialWorkspace(workSpace);
        user.setOfficialWorkspace(officialWorkspace);
        userRepository.save(user);
        return officialWorkspace;
    }


    private boolean existByName(String name) {
        return personalWorkspaceRepository.existsByName(name)
                || officialWorkspaceRepository.existsByName(name);
    }

    private PersonalWorkspace savePersonalWorkspace(PersonalWorkspace workSpace) {
        return personalWorkspaceRepository.save(workSpace);
    }

    private OfficialWorkspace saveOfficialWorkspace(OfficialWorkspace workSpace) {
        return officialWorkspaceRepository.save(workSpace);
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private boolean userAlreadyExistInWorkspace(Set<User> members, Set<User> moderators, String email, String ownerEmail) {
        boolean memberExist = members.stream().anyMatch(user -> user.getEmail().equals(email));
        boolean moderatorExist = moderators.stream().anyMatch(user -> user.getEmail().equals(email));
        return memberExist || moderatorExist || ownerEmail.equals(email);
    }


    private User getAUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
    }

    private User getAUserByUserId(Long id) throws UserException {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
    }

    private boolean isValidToken(LocalDateTime expiryDate) {
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), expiryDate);
        return minutes <= 0;
    }
}
