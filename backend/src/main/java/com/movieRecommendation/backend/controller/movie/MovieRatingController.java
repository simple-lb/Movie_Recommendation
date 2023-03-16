package com.movieRecommendation.backend.controller.movie;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;

@RestController
public class MovieRatingController {
    @PostMapping("/api/movie/getRating/")
    public List<Map<String, String>> getRating(@RequestParam Map<String, String> data) throws IOException {
        ArrayList<Map<String, String>> res = new ArrayList<>();
        String userId = data.get("userId");
        String fileName = "ratings/user" + userId + "Rating.dat";
        File file = new File(fileName);
        if(file.exists()){
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = in.read()) != -1) {
                sb.append((char) ch);
            }
            in.close();
            String[] tmp = sb.toString().split("\n");
            for(int i = 0 ; i < tmp.length ; i++){
                String[] split = tmp[i].split("::");
                HashMap<String, String> map = new HashMap<>();
                map.put("userId",split[0]);
                map.put("movieId",split[1]);
                map.put("rate",split[2]);
                res.add(map);
            }
        }
        return res;
    }
}
