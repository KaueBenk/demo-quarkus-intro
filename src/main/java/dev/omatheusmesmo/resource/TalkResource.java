package dev.omatheusmesmo.resource;

import java.net.URI;
import java.util.List;

import dev.omatheusmesmo.dto.TalkRequest;
import dev.omatheusmesmo.dto.TalkResponse;
import dev.omatheusmesmo.model.Level;
import dev.omatheusmesmo.model.Talk;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/talks")
public class TalkResource {

    @GET
    public List<TalkResponse> list() {
        return Talk.<Talk>listAll().stream()
                .map(TalkResponse::from)
                .toList();
    }

    @GET
    @Path("/{id}")
    public TalkResponse get(@PathParam("id") Long id) {
        Talk talk = Talk.findById(id);
        if (talk == null) {
            throw new NotFoundException();
        }
        return TalkResponse.from(talk);
    }

    @GET
    @Path("/search")
    public List<TalkResponse> search(@RestQuery Level level) {
        return Talk.findByLevel(level).stream()
                .map(TalkResponse::from)
                .toList();
    }

    @POST
    @Transactional
    public Response create(@Valid TalkRequest req) {
        Talk talk = new Talk(req.title(), req.speaker(),
            req.startTime(), req.endTime(), req.level());
        talk.persist();
        return Response.created(URI.create("/talks/" + talk.id))
                .entity(TalkResponse.from(talk))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public TalkResponse update(@PathParam("id") Long id, @Valid TalkRequest req) {
        Talk talk = Talk.findById(id);
        if (talk == null) {
            throw new NotFoundException();
        }
        talk.updateFrom(req.title(), req.speaker(),
            req.startTime(), req.endTime(), req.level());
        return TalkResponse.from(talk);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        Talk talk = Talk.findById(id);
        if (talk == null) {
            throw new NotFoundException();
        }
        talk.delete();
    }
}
