package com.movieRecommendation.backend.controller.movie;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Date;
import java.util.Map;

@RestController
public class RateController {
    @PostMapping("/api/movie/rate/")
    public void rate(@RequestParam Map<String, String> data) throws IOException {
        String movieId = data.get("movieId");
        String userId = data.get("userId");
        String rate = data.get("rate");
        String fileName = "ratings/user" + userId + "Rating.dat";
        String time = String.valueOf(new Date().getTime());

        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            out.write(userId + "::" + movieId + "::" + rate + "::" + time + "\n");
            out.close();
        } else {
            BufferedReader in = new BufferedReader(new FileReader(fileName));
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = in.read()) != -1) {
                sb.append((char) ch);
            }
            String[] tmp = sb.toString().split("\n");
            in.close();
            boolean flag = false;
            for (int i = 0; i < tmp.length; i++) {
                if (movieId.equals(tmp[i].split("::")[1])) {
                    flag = true;
                    tmp[i] = userId + "::" + movieId + "::" + rate + "::" + time;
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < tmp.length; i++) {
                out.write(tmp[i] +"\n");
            }
            if (!flag)
                out.write(userId + "::" + movieId + "::" + rate + "::" + time + "\n");
            out.close();
        }
    }
}
