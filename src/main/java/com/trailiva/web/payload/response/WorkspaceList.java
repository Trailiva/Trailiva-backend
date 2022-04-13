package com.trailiva.web.payload.response;

import com.trailiva.data.model.WorkSpace;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class WorkspaceList extends RepresentationModel<WorkspaceList> {
    private List<WorkSpace> workSpaces = new ArrayList<>();
}
