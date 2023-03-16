package com.movieRecommendation.backend.controller.movie;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.*;

@RestController
public class SearchController {
    @PostMapping("/api/movie/search/")
    public List<Map<String, String>> search(@RequestParam Map<String, String> data) throws IOException {
        String title = data.get("title");
        String jsonStr = "";
        File jsonFile = new File("/home/hadoop/backend/src/main/resources/movies.json");
        FileReader fileReader = new FileReader(jsonFile);
        Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
        int ch = 0;
        StringBuffer sb = new StringBuffer();
        while ((ch = reader.read()) != -1) {
            sb.append((char) ch);
        }

        fileReader.close();
        reader.close();
        jsonStr = sb.toString();
        JSONObject jobj = JSON.parseObject(jsonStr);
        JSONArray movies = jobj.getJSONArray("movies");
        List m = new ArrayList();
        for (int i = 0; i < movies.size(); i++) {
            JSONObject tmp = (JSONObject) movies.get(i);
            if (((String) tmp.get("title")).contains(title)) {
                Map<String, String> map = new HashMap<>();
                map.put("picture", tmp.get("picture").toString());
                map.put("title", tmp.get("title").toString());
                if (tmp.get("rate") == null)
                    map.put("rate", "暂无评");
                else
                    map.put("rate", tmp.get("rate").toString());
                map.put("id", tmp.get("cnt").toString());
                map.put("year", tmp.get("year").toString());
                map.put("desc",getDesc(tmp));
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
