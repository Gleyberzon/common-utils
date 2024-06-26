package secretsManager;

import Store.Store;
import Store.StoreImp;
import apiUtils.RestApi;
import com.google.gson.Gson;
import Enumerations.MessageLevel;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static propertyUtils.PropertyUtils.getGlobalProperty;
import static ReportUtils.Report.reportAndLog;

/**
 * This class runs API operations (currently only getSecret/s) against Akeyless secret manager.
 * The public getSecret method does not return the immediate secret, it returns the referred secret.
 */
public class SecretUtils {
    private static final Logger logger= LoggerFactory.getLogger(SecretUtils.class);

    /**
     *
     * This method receives a full path to a Secret which its value refers to another secret.
     * Then it's getting the referred secret (by get-secret-value API).
     * See Akeyless documentation for further details: https://docs.akeyless.io/reference/
     *
     * @param projectName - project name as it appears in Akeyless
     * @param environment - environment name as it appears in Akeyless
     * @param accessId - access id from Akeyless "Auth Methods"
     * @param accessKey - access key from Akeyless "Auth Methods"
     * @param secretName - secret name
     * @return a Secret containing of full secret path and password value
     * @throws ParseException
     * @author sela.zvika
     * @since 02.19.23
     */
    public static Secret getSecret(String projectName, String environment,String accessId,String accessKey,String secretName) throws ParseException {

        if (SecretsStoreManager.getStore().getValueFromStore(secretName)!=null){
            logger.info(String.format("Secret %s, retrieved from cache",secretName));
            return SecretsStoreManager.getStore().getValueFromStore(secretName);
        }

        //get secret ref
        String token = getToken(accessId,accessKey);
        //example: /qaautomation/nonprod/qa0/l2a/SecretRef/nonHeaderuser1
        String secretRef = String.format("/qaautomation/nonprod/%s/%s/SecretRef/%s",environment,projectName,secretName);
        //get secret value from secret ref
        Secret refSecret = getSingleSecret(secretRef,token);
        //get actual secret
        Secret actualSecret = getSingleSecret(refSecret.getPassword(),token);
        reportAndLog("username is "+ actualSecret.getUsername(),MessageLevel.INFO);
        //enter the Secret to our cache
        synchronized (SecretUtils.class) {
            SecretsStoreManager.getStore().putValueInStore(secretName, actualSecret);
        }

        return actualSecret;
    }

    /**
     * sends access id and key to Akeyless to get the authorization token
     * @param accessId - access id from Akeyless "Auth Methods"
     * @param accessKey - access key from Akeyless "Auth Methods"
     * @return token (on success)
     * @throws ParseException
     * @author sela.zvika
     * @since 02.19.23
     */
    private static String getToken(String accessId,String accessKey) throws ParseException {

        //get token
        RestApi restApi = new RestApi();
        HashMap<String,String> credentials = new HashMap<>();
        credentials.put("access-id",accessId);
        credentials.put("access-key",accessKey);
        HashMap<String,String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        restApi.setRequestHeaders(headers);

        Response response = restApi.post(new Gson().toJson(credentials), getGlobalProperty("akeyless_nonprod_api_url")+
                "/auth");
        if(response.getStatusCode()!=200){
            reportAndLog("Failed to retrieve token", MessageLevel.ERROR);
            if (response !=null) {
                reportAndLog("Response Status code is " + response.getStatusCode(), MessageLevel.ERROR);
                reportAndLog("Response body is " + response.getBody().toString(), MessageLevel.ERROR);
            }
            throw new Error("Failed to retrieve token");
        }
        else{
            reportAndLog("Token retrieved from Secret Manager", MessageLevel.INFO);
        }
        return  response.jsonPath().getString("token");
    }

    /**
     * Retrieves a Secret from Akeyless from the fullpath
     * @param secretFullPath - full path to Akeyless's secret
     * @param token - authorization token
     * @return Secret containing of secret full path and password's value
     * @throws ParseException
     *
     * @author sela.zvika
     * @since 02.19.23
     */
    private static Secret getSingleSecret(String secretFullPath,String token) throws ParseException {

            HashMap<String, Object> data = new HashMap<>();
            data.put("names", new String[]{secretFullPath});
            data.put("token", token);
            RestApi restApi = new RestApi();
            Response response = restApi.post(new Gson().toJson(data), getGlobalProperty("akeyless_nonprod_api_url") +
                    "/get-secret-value");
            if (response.getStatusCode() != 200) {
                reportAndLog("Failed to get Secret " + secretFullPath, MessageLevel.ERROR);
                if (response !=null) {
                    reportAndLog("Response Status code is " + response.getStatusCode(), MessageLevel.ERROR);
                    reportAndLog("Response body is " + response.getBody().toString(), MessageLevel.ERROR);
                }
                throw new Error("Failed to retrieve secret");
            } else {
                logger.info("secret retrieved successfully from Secret Manager", MessageLevel.INFO);
            }

            JSONObject jsonObject = response.as(JSONObject.class);
            return new Secret(secretFullPath, (String) jsonObject.get(secretFullPath));

    }

    /**
     * Retrieves a Secret's tag from Akeyless from the fullpath
     * @param secretFullPath - full path to Akeyless's secret
     * @param token - authorization token
     * @return Optional<String[]> containing of the secret's tag
     * @throws ParseException
     *
     * @author sela.zvika
     * @since 02.19.23
     */
    private static Optional getSingleSecretTags(String secretFullPath,String token) throws ParseException {

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", secretFullPath);
        data.put("token", token);
        RestApi restApi = new RestApi();
        Response response = restApi.post(new Gson().toJson(data), getGlobalProperty("akeyless_nonprod_api_url") +
                "/get-tags");
        if (response.getStatusCode() != 200) {
            reportAndLog("Failed to get Secret " + secretFullPath, MessageLevel.ERROR);
            if (response !=null) {
                reportAndLog("Response Status code is " + response.getStatusCode(), MessageLevel.ERROR);
                reportAndLog("Response body is " + response.getBody().toString(), MessageLevel.ERROR);
            }
            throw new Error("Failed to retrieve secret");
        } else {
            logger.info("tags retrieved successfully from Secret Manager", MessageLevel.INFO);
        }

        String[] tagsArray = response.as(String[].class);

        return Optional.ofNullable(tagsArray);

    }

    /**
     * Retrieve multiple secret references:
     * Each Secret is expected to be a reference to another (actual) secret.
     * The method will retrieve the actual Secret and aggregate it into a List
     *
     * @param projectName - project name as it appears in Akeyless
     * @param environment - environment name as it appears in Akeyless
     * @param accessId - access id from Akeyless "Auth Methods"
     * @param accessKey - access key from Akeyless "Auth Methods"
     * @param secretNames - 1 or more secret names
     * @return a List of secrets
     * @throws ParseException
     *
     * @author sela.zvika
     * @since 02.19.23
     */
    public static List<Secret> getSecrets(String projectName, String environment, String accessId, String accessKey, String... secretNames) throws ParseException {
        List<Secret> allSecrets = new ArrayList<>();

        for (String secretName:secretNames){
            allSecrets.add(getSecret(projectName,environment,accessId,accessKey,secretName));
        }
        return allSecrets;

    }

    /**
     *
     * This method receives a full path to a Secret which its value refers to another secret.
     * Then it's getting the referred secret (by get-secret-value API).
     * Then it return that secret's tags (by get-tags API)
     * See Akeyless documentation for further details: https://docs.akeyless.io/reference/
     *
     * @param projectName - project name as it appears in Akeyless
     * @param environment - environment name as it appears in Akeyless
     * @param accessId - access id from Akeyless "Auth Methods"
     * @param accessKey - access key from Akeyless "Auth Methods"
     * @param secretName - secret name
     * @return Optional<jsonArray> of the tags
     * @throws ParseException
     * @author sela.zvika
     * @since 02.19.23
     */
    public static Optional<String[]> getSecretTags(String projectName, String environment, String accessId, String accessKey, String secretName) throws ParseException {

        //get secret ref
        String token = getToken(accessId,accessKey);
        //example: /qaautomation/nonprod/qa0/l2a/SecretRef/nonHeaderuser1
        String secretRef = String.format("/qaautomation/nonprod/%s/%s/SecretRef/%s",environment,projectName,secretName);
        //get secret value from secret ref
        Secret refSecret = getSingleSecret(secretRef,token);
        //get actual secret
        Secret actualSecret = getSingleSecret(refSecret.getPassword(),token);
        //get the secret's tags
        Optional tags = getSingleSecretTags(actualSecret.getFullSecretPath(), token);
        if (tags.isPresent())
            reportAndLog("tags are "+ Arrays.toString((String[])tags.get()),MessageLevel.INFO);

        return tags;
    }

    /**
     * Global store to cache our secrets to prevent repetitive requests to Akeyless server<br>
     * <b>Example:</b><br>
     *
     *
     * <pre>
     * {@code
     *
     *         Secret secret = SecretUtils.get("nonHeaderUser01");
     *         StoreManager.getStore().putValueInStore("nonHeaderUser01", secret);
     *         String username = ((Secret)StoreManager.getStore().getValueFromStore("nonHeaderUser01")).getUsername();
     *
     *  }
     * </pre>
     *
     * @author Tzvika Sela
     * @see Store
     * @see StoreImp
     * @since 0.0.2 (Jan 2022)
     */

    private static class SecretsStoreManager {

        private static final Store secretsStoreMap = new StoreImp();
        private static final Logger logger = LoggerFactory.getLogger(SecretsStoreManager.class);

        private SecretsStoreManager() {
        }


        /**
         * Retrieve store from the store object, according to store type selected.
         * If there is no store, which previously registered, it will create a new store and retrieve it
         *
         * @return Store object, according to type selected
         */

        public static synchronized Store getStore() {
            return secretsStoreMap;
        }
    }//inner class





    }