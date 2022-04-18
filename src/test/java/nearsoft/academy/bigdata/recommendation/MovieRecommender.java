package nearsoft.academy.bigdata.recommendation;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MovieRecommender{
    private DataModel model;
    private UserSimilarity similarity;
    private UserNeighborhood neighborhood;
    private UserBasedRecommender recommender;
    HashMap<String, Integer> usersMap = new HashMap<String, Integer>();
    HashMap<String, Integer> productsMap = new HashMap<String, Integer>();
    int totalReviews = 0;

    MovieRecommender(String path) throws IOException, TasteException {
        this.model = new FileDataModel(createCSV(path,false));
        if(usersMap.isEmpty() || productsMap.isEmpty())
            fillMaps();
        this.similarity = new PearsonCorrelationSimilarity(model);
        this.neighborhood = new ThresholdUserNeighborhood(0.1,similarity,model);
        this.recommender = new GenericUserBasedRecommender(model,neighborhood, similarity);
    }
    public int getTotalReviews() throws TasteException{
        return totalReviews;
    }
    public int getTotalProducts() throws TasteException{
        return model.getNumItems();
    }
    public int getTotalUsers() throws TasteException{
        return model.getNumUsers();
    }
    public List<String> getRecommendationsForUser(String user) throws TasteException{
        List<RecommendedItem> recommendations = recommender.recommend(usersMap.get(user), 3);
        List<String> recs = new ArrayList<String>();
        for (RecommendedItem recommendation : recommendations){
            recs.add(getProduct(recommendation.getItemID()));
        }
        return recs;
    }
    
    private String getProduct(long id){
        for(String key: productsMap.keySet()){
            if(productsMap.get(key) == id){
                return key;
            }
        }
        return null;
    }
    private void fillMaps() throws NumberFormatException, IOException{
        BufferedReader brUser = new BufferedReader(new FileReader("data/users.csv"));
        String line;
        while ((line = brUser.readLine()) != null) {
            String[] values = line.split(",");
            usersMap.put(values[0], Integer.parseInt(values[1]));
        }
        BufferedReader brProduct = new BufferedReader(new FileReader("data/products.csv"));
        while ((line = brProduct.readLine()) != null) {
            String[] values = line.split(",");
            productsMap.put(values[0], Integer.parseInt(values[1]));
        }
        brUser.close();
        brProduct.close();
    }
    private File createCSV(String path,boolean reaload) throws IOException{
        File csv = new File("data/movies.csv");
        if((!csv.isFile())||(csv.isFile()&&reaload)){
            InputStream is = new GZIPInputStream(new FileInputStream(path));
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            FileWriter fw = new FileWriter(csv);
            BufferedWriter bw = new BufferedWriter(fw);

            File users = new File("data/users.csv");
            FileWriter fwUser = new FileWriter(users);
            BufferedWriter bwUser = new BufferedWriter(fwUser);
            File products = new File("data/products.csv");
            FileWriter fwProduct = new FileWriter(products);
            BufferedWriter bwProduct = new BufferedWriter(fwProduct);
            

            String product = "";
            String user = "";
            String score = "";
            
            String currentLine;
            while((currentLine = br.readLine()) != null){
                if(currentLine.contains("product/productId: ")){
                    product = currentLine.split(" ")[1];
                    if(!productsMap.containsKey(product)){
                        productsMap.put(product, productsMap.size()+1);
                        bwProduct.write(product + "," + productsMap.get(product) + "\n");
                    }
                }
                else if(currentLine.contains("review/userId: ")){
                    user = currentLine.split(" ")[1];
                    if(!usersMap.containsKey(user)){
                        usersMap.put(user, usersMap.size()+1);
                        bwUser.write(user + "," + usersMap.get(user) + "\n");
                    }
                }
                else if(currentLine.contains("review/score: ")) {
                    score = currentLine.split(" ")[1];
                    bw.write(usersMap.get(user) + "," + productsMap.get(product) + "," + score + "\n");
                    totalReviews++;
                }
            }
            bwProduct.close();
            fwProduct.close();
            bwUser.close();
            fwUser.close();
            bw.close();
            fw.close();
            br.close();
            is.close();
        }
        else{
            FileReader fr = new FileReader(csv);
            BufferedReader br = new BufferedReader(fr);
            while (br.readLine() != null) totalReviews++;
            br.close();
            fr.close();
        }
        return csv;
    }
}
