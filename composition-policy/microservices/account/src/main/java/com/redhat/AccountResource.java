package microservices.account.src.main.java.com.redhat;

import java.math.BigDecimal;
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

@Path("/account")
public class AccountResource {
    Set<Account> accounts = new HashSet<>();

    public AccountResource(){
        Account gusAccount = new Account();
        gusAccount.id = "123";
        gusAccount.bankAgency = "001";
        gusAccount.bankAccount = "34524";

        Account carlosAccount = new Account();
        carlosAccount.id = "456";
        carlosAccount.bankAgency = "002";
        carlosAccount.bankAccount = "54521";
        
        accounts.add(gusAccount);
        accounts.add(carlosAccount);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Account> getAll(@QueryParam("delay") Long delay) {
        if(delay != null) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return accounts;
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
        
        List<Account> account = this.accounts.stream().filter((p)->id.equals(p.id)).collect(Collectors.toCollection(ArrayList::new));
        
        if (!account.isEmpty())
            return Response.ok(account.get(0)).build();
        
        return Response.status(404).build();        
    }
}
