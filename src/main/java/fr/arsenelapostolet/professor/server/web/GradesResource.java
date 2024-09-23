package fr.arsenelapostolet.professor.server.web;

import fr.arsenelapostolet.professor.server.core.application.GradesApplication;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("grades")
public class GradesResource {

    private final GradesApplication application;

    @Inject
    public GradesResource(GradesApplication application) {
        this.application = application;
    }

    @POST
    @Path("classes/{name}")
    public Response createClass(
            @PathParam("name") String name,
            String body
    ) {
        return Response
                .status(200)
                .entity(application.createClass(name, body.lines()))
                .build();
    }
}
