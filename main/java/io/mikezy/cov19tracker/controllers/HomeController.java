package io.mikezy.cov19tracker.controllers;

import io.mikezy.cov19tracker.models.LocationStats;
import io.mikezy.cov19tracker.services.Cov19DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    Cov19DataService cov19DataService;

    @GetMapping("/")
    public String home(Model model) {

        List<LocationStats> allStats = cov19DataService.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();

        model.addAttribute("locationStats", cov19DataService.getAllStats());
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);
        model.addAttribute("curDate", cov19DataService.getAllStats());
        model.addAttribute("totalRecovered", cov19DataService.getAllRecovered());
        model.addAttribute("totalDeath", cov19DataService.getAllDeath());
        model.addAttribute("totalLatestDeath", cov19DataService.getAllStats());
        return "home";
    }
}
