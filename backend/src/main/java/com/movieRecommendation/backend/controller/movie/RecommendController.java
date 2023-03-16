package com.movieRecommendation.backend.controller.movie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RecommendController {
    @PostMapping("/api/movie/recommend")
    public List<Map<String, String>> recommend(@RequestParam Map<String, String> data) throws InterruptedException, IOException {
        String userId = data.get("userId");
        String type = data.get("type");
        String ratingFile = "ratings/user" + userId + "Rating.dat";
        String resultFile = "results/result" + userId + ".txt";
        List m = new ArrayList();
        int status = 0;
        if (type.equals("true")) {
            String[] cmd = {"/home/hadoop/backend/src/main/resources/submit.sh", ratingFile, resultFile};
            Process process = Runtime.getRuntime().exec(cmd);
            status = process.waitFor();
        }
        if (status != 0) {
            System.err.println("Failed to call shell's command and the return status's is: " + status);
        } else {
            String jsonStr = "";
            File jsonFile = new File("/home/hadoop/backend/src/main/resources/movies.json");
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch1;
            StringBuffer sb1 = new StringBuffer();
            while ((ch1 = reader.read()) != -1) {
                sb1.append((char) ch1);
            }

            fileReader.close();
            reader.close();
            jsonStr = sb1.toString();
            JSONObject jobj = JSON.parseObject(jsonStr);
            JSONArray movies = jobj.getJSONArray("movies");

            BufferedReader in = new BufferedReader(new FileReader(resultFile));
            int ch2;
            StringBuffer sb2 = new StringBuffer();
            while ((ch2 = in.read()) != -1) {
                sb2.append((char) ch2);
            }
            String[] tmp = sb2.toString().split("\n");
            in.close();
            for (int i = 2; i < tmp.length; i++) {
                int id = Integer.parseInt(tmp[i].split(":")[1]);
                String value = tmp[i].split(":")[2].substring(0,3);
                JSONObject movie = (JSONObject) movies.get(id - 1);
                Map<String, String> map = new HashMap<>();
                map.put("picture", movie.get("picture").toString());
                map.put("title", movie.get("title").toString());
                if (movie.get("rate") == null)
                    map.put("rate", "暂无评");
                else
                    map.put("rate", movie.get("rate").toString());
                map.put("id", movie.get("cnt").toString());
                map.put("year", movie.get("year").toString());
                map.put("desc",getDesc(movie));
                map.put("value",value);
                m.add(map);
            }
        }
        return m;
    }
    public String getDesc(JSONObject jsonObject){
        String year = jsonObject.getString("year");
        String[] regions = jsonObject.getJSONArray("region").get(0).toString().split("/");
        String region = regions[0].trim();
        for(int i = 1 ; i < 2 && i < regions.length ; i++){
            region += " 、 " + regions[i].trim();
        }
        String type = jsonObject.getJSONArray("type").get(0).toString();
        String res = year + "  /  " +  region + "  /  " + type + "  /  ";
        JSONArray actorArray = jsonObject.getJSONArray("actor");
        String actor = actorArray.get(0).toString();
        for(int i = 1 ; i < 2 && i < actorArray.size() ; i++){
            actor += " 、 " + actorArray.get(i).toString();
        }
        return res += actor;
    }
}