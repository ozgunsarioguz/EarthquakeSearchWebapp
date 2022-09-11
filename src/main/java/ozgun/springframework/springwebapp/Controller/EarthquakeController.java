package ozgun.springframework.springwebapp.Controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import ozgun.springframework.springwebapp.Model.Earthquake;
import ozgun.springframework.springwebapp.Service.EarthquakeService;

@Controller
public class EarthquakeController {

    private String country;
    private int days;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    @GetMapping("/earthquakes")
    public String inputSubmit() {

        return "getinput";
    
    }
    @PostMapping("/response")
    public String saveInput(Model model, String country, int days) {
        this.days = days;
        this.country = country;
        List<Earthquake> queryResult = EarthquakeService.getEarthquakes(country.toLowerCase(), this.days);
        model.addAttribute("days",days);
        model.addAttribute("country",country.toUpperCase());

        if(!queryResult.isEmpty()){
            model.addAttribute("data",queryResult);

            System.out.println("records found");
            System.out.println(queryResult);
        }
        return "listdata";
    }
  
}
