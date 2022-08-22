package com.trailiva.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.trailiva.data.model.OfficialWorkspace;
import com.trailiva.data.model.PersonalWorkspace;
import com.trailiva.data.model.User;
import com.trailiva.data.model.WorkspaceRequestToken;
import com.trailiva.data.repository.*;
import com.trailiva.web.exceptions.TokenException;
import com.trailiva.web.exceptions.UserException;
import com.trailiva.web.exceptions.WorkspaceException;
import com.trailiva.web.payload.request.WorkspaceRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.trailiva.data.model.TokenType.WORKSPACE_REQUEST;
import static com.trailiva.util.Helper.convertMultiPartToFile;
import static com.trailiva.util.Helper.isValidToken;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

    private final ModelMapper modelMapper;
    private final PersonalWorkspaceRepository personalWorkspaceRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OfficialWorkspaceRepository officialWorkspaceRepository;
    private final WorkspaceRequestTokenRepository tokenRepository;
    private final EmailService emailService;

    public WorkspaceServiceImpl(
            ModelMapper modelMapper,
            PersonalWorkspaceRepository personalWorkspaceRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            OfficialWorkspaceRepository officialWorkspaceRepository,
            WorkspaceRequestTokenRepository tokenRepository, EmailService emailService) {
        this.modelMapper = modelMapper;
        this.personalWorkspaceRepository = personalWorkspaceRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.officialWorkspaceRepository = officialWorkspaceRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }


    @Override
    public void addContributorToOfficialWorkspace(List<String> contributorEmails, Long userId) throws UserException, WorkspaceException {
        if (!contributorEmails.isEmpty()) {
            for (String email : contributorEmails) {
                sendRequestToken(userId, email);
            }
        }

    }

    @Override
    public void addModeratorToOfficialWorkspace(List<String> moderatorEmail, Long userId) throws UserException, WorkspaceException {
        if (!moderatorEmail.isEmpty()) {
            for (String email : moderatorEmail) {
                sendRequestToken(userId, email);
            }
        }
    }


    @Override
    public void addContributorToWorkspaceFromCSV(MultipartFile file, Long userId) throws IOException,
            CsvValidationException, UserException, WorkspaceException {
        CSVReader reader = new CSVReader(new FileReader(convertMultiPartToFile(file)));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            for (String email : nextLine) {
                sendRequestToken(userId, email);
            }
        }
    }

    @Override
    public void addModeratorToWorkspaceFromCSV(MultipartFile file, Long userId) throws IOException,
            CsvValidationException, UserException, WorkspaceException {
        CSVReader reader = new CSVReader(new FileReader(convertMultiPartToFile(file)));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            for (String email : nextLine) {
                sendRequestToken(userId, email);
            }
        }
    }

    @Override
    public void addContributorToWorkspace(String requestToken) throws TokenException, UserException {
        WorkspaceRequestToken token = getToken(requestToken, WORKSPACE_REQUEST.toString());
        if (isValidToken(token.getExpiryDate())) throw new TokenException("Token has expired");
        onboardContributor(token.getWorkspace(), token.getUser());
        tokenRepository.delete(token);
    }

    @Override
    public void addModeratorToWorkspace(String requestToken) throws TokenException, UserException {
        WorkspaceRequestToken token = getToken(requestToken, WORKSPACE_REQUEST.toString());
        if (isValidToken(token.getExpiryDate())) throw new TokenException("Token has expired");
        onboardModerator(token.getWorkspace(), token.getUser());
        tokenRepository.delete(token);
    }

    @Override
    public void removeContributorFromWorkspace(Long userId, Long contributorId) throws UserException, WorkspaceException {
        OfficialWorkspace workspace = getUserOfficialWorkspace(userId);
        User contributor = getAUserByUserId(contributorId);
        workspace.getModerators().remove(contributor);
    }

    @Override
    public void removeModeratorFromWorkspace(Long userId, Long moderatorId) throws UserException, WorkspaceException {
        OfficialWorkspace workspace = getUserOfficialWorkspace(userId);
        User moderator = getAUserByUserId(moderatorId);
        workspace.getModerators().remove(moderator);
    }

    @Override
    public int countOfficialWorkspaceProject(Long workspaceId) throws WorkspaceException {
        OfficialWorkspace workspace = getOfficialWorkspace(workspaceId);
        return workspace.getProjects().size();
    }

    @Override
    public int countPersonalWorkspaceProject(Long workspaceId) throws WorkspaceException {
        PersonalWorkspace workspace = getPersonalWorkspace(workspaceId);
        return workspace.getProjects().size();
    }

    @Override
    public int countContributorOnOfficialWorkspace(Long workspaceId) throws WorkspaceException {
        OfficialWorkspace workspace = getOfficialWorkspace(workspaceId);
        return workspace.getContributors().size();
    }

    @Override
    public int countModeratorOnOfficialWorkspace(Long workspaceId) throws WorkspaceException {
        OfficialWorkspace workspace = getOfficialWorkspace(workspaceId);
        return workspace.getModerators().size();
    }

    @Override
    public List<PersonalWorkspace> getPersonalWorkspaces() {
        return personalWorkspaceRepository.findAll();
    }

    @Override
    public List<OfficialWorkspace> getOfficialWorkspaces() {
        return officialWorkspaceRepository.findAll();
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
    public OfficialWorkspace getUserOfficialWorkspace(Long userId) throws WorkspaceException, UserException {
        User creator = getAUserByUserId(userId);
        return officialWorkspaceRepository.findByCreator(creator)
                .orElseThrow(() -> new WorkspaceException("Workspace not found"));
    }

    @Override
    public PersonalWorkspace getUserPersonalWorkspace(Long userId) throws UserException, WorkspaceException {
        User creator = getAUserByUserId(userId);
        return personalWorkspaceRepository.findByCreator(creator)
                .orElseThrow(() -> new WorkspaceException("Workspace not found"));
    }


    @Override
    public PersonalWorkspace getPersonalWorkspace(Long workspaceId) throws WorkspaceException {
        return personalWorkspaceRepository.findById(workspaceId).orElseThrow(
                () -> new WorkspaceException("Workspace not found"));
    }


    private void onboardContributor(OfficialWorkspace workspace, User contributor) throws UserException {
        if (userAlreadyExistInWorkspace(workspace.getContributors(),
                workspace.getModerators(), contributor.getEmail(), workspace.getCreator().getEmail())) {
            throw new UserException("Contributor with email " + contributor.getEmail() + " already added to this workspace");
        }
        workspace.getContributors().add(contributor);
        saveOfficialWorkspace(workspace);
    }

    private void onboardModerator(OfficialWorkspace workspace, User moderator) throws UserException {
        if (userAlreadyExistInWorkspace(workspace.getContributors(),
                workspace.getModerators(), moderator.getEmail(), workspace.getCreator().getEmail())) {
            throw new UserException("Contributor with email " + moderator.getEmail() + " already added to this workspace");
        }
        moderator.getRoles().add(roleRepository.findByName("ROLE_MODERATOR").get());
        workspace.getModerators().add(moderator);
        saveOfficialWorkspace(workspace);
    }


    @Override
    public PersonalWorkspace createPersonalWorkspace(WorkspaceRequest request, Long userId) throws UserException, WorkspaceException {
        User user = getAUserByUserId(userId);
        if (existByName(request.getName()))
            throw new WorkspaceException("Workspace with name already exist");
        PersonalWorkspace workSpace = modelMapper.map(request, PersonalWorkspace.class);
        workSpace.setCreator(user);
        return savePersonalWorkspace(workSpace);
    }

    @Override
    public OfficialWorkspace createOfficialWorkspace(WorkspaceRequest request, Long userId) throws UserException, WorkspaceException {
        User user = getAUserByUserId(userId);
        if (existByName(request.getName()))
            throw new WorkspaceException("Workspace with name already exist");
        user.getRoles().add(roleRepository.findByName("ROLE_SUPER_MODERATOR").get());
        OfficialWorkspace workSpace = modelMapper.map(request, OfficialWorkspace.class);
        workSpace.setCreator(user);
        return saveOfficialWorkspace(workSpace);
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

    private boolean userAlreadyExistInWorkspace(Set<User> contributors, Set<User> moderators, String email, String ownerEmail) {
        boolean contributorExist = contributors.stream().anyMatch(user -> user.getEmail().equals(email));
        boolean moderatorExist = moderators.stream().anyMatch(user -> user.getEmail().equals(email));
        return contributorExist || moderatorExist || ownerEmail.equals(email);
    }


    private User getAUserByEmail(String email) throws UserException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
    }

    private User getAUserByUserId(Long id) throws UserException {
        return userRepository.findById(id).orElseThrow(() -> new UserException("User not found"));
    }



    private void sendRequestToken(Long userId, String email) throws UserException, WorkspaceException {
        User user = getAUserByEmail(email);
        String token = UUID.randomUUID().toString();
        OfficialWorkspace workspace = getUserOfficialWorkspace(userId);

        WorkspaceRequestToken requestToken = new WorkspaceRequestToken(token, user, WORKSPACE_REQUEST.toString(),
                workspace);
        tokenRepository.save(requestToken);
        emailService.sendWorkspaceRequestTokenEmail(email, requestToken.getToken());
    }
}