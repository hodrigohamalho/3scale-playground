package microservices.person.src.main.java.com.redhat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/person")
public class PersonResource {

    Set<Person> people = new HashSet<>();

    public PersonResource(){
        Person gus = new Person();
        gus.id = "123";
        gus.city = "Brasilia";
        gus.name = "Gustavo";
        gus.phone = "556112343234";
        gus.state = "DF";

        Person carlos = new Person();
        carlos.id = "456";
        carlos.city = "Brasilia";
        carlos.name = "Carlos";
        carlos.phone = "556124343276";
        carlos.state = "DF";

        people.add(gus);
        people.add(carlos);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Person> getAll(@QueryParam("delay") Long delay) {
        if(delay != null) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return people;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response retrive(@PathParam("id") String id, @QueryParam("delay") Long delay) {
        if(delay != null) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        List<Person> people = this.people.stream().filter((p)->id.equals(p.id)).collect(Collectors.toCollection(ArrayList::new));
        
        if (!people.isEmpty())
            return Response.ok(people.get(0)).build();
        
        return Response.status(404).build();        
    }
    
}
