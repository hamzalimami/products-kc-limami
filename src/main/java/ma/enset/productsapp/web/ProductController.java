package ma.enset.productsapp.web;

import lombok.Data;
import ma.enset.productsapp.repositories.ProductRepository;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.AdapterDeploymentContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.spi.HttpFacade;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.facade.SimpleHttpFacade;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ProductController{
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private KeycloakRestTemplate keycloakRestTemplate;
    @Autowired
    private AdapterDeploymentContext adapterDeploymentContext;

    @GetMapping("/")
    public String index(){

        return "index";
    }
    @GetMapping("/products")
    public String products(Model model){
        model.addAttribute("products",productRepository.findAll());
        return "products";
    }
    @GetMapping("/suppliers")
    public String suppliers(Model model){
     PagedModel<Supplier>pagesuppliers = keycloakRestTemplate.getForObject("http://localhost:8083/suppliers/",PagedModel.class);
        model.addAttribute("suppliers",pagesuppliers);
        return "suppliers";
    }
    @GetMapping("/jwt")
    @ResponseBody
    //Récuperer l'access token
    public Map<String,String> map(HttpServletRequest request){
        //user principal = user authentifié
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        //casting pour recuperer l'objet de type keycloak principal
        KeycloakPrincipal principal=(KeycloakPrincipal) token.getPrincipal();
        //keycloak contecxt c'est l a ou on stocke les infos sur les users authentifiés
        KeycloakSecurityContext keycloakSecurityContext =principal.getKeycloakSecurityContext();
        //consulter le jwt
        Map<String,String> map = new HashMap<>();
        map.put("access_token",keycloakSecurityContext.getTokenString());
        return map;

        //keycloakRestTemplate il me permet a chaque fois d'jouter le header authorization
    }
    //pour cacher les exceptions aux clients parce que a partir des exceptions on risque être piraté
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e,Model model){
        model.addAttribute("errormessage","probléme d'autorisation");
        return "errors";
    }
    @GetMapping("/profile")
    public String changePassword(RedirectAttributes attributes, HttpServletRequest request, HttpServletResponse
            response) {
        HttpFacade facade = new SimpleHttpFacade(request, response);
        KeycloakDeployment deployment = adapterDeploymentContext.resolveDeployment(facade);
        attributes.addAttribute("referrer", deployment.getResourceName());
        attributes.addAttribute("referrer_uri",request.getHeader("referer"));
        return "redirect:" + deployment.getAccountUrl();
    }



}
@Data
class Supplier {
    private long id;
    private String name;
    private String email;



}
