package com.telekom.m2m.cot.restsdk.inventory;

import com.google.gson.Gson;
import com.telekom.m2m.cot.restsdk.CloudOfThingsRestClient;
import com.telekom.m2m.cot.restsdk.util.Filter;
import com.telekom.m2m.cot.restsdk.util.GsonUtils;

/**
 * Store and retrieve binaries in the CoT inventory.
 */
public class BinariesApi {

    private final CloudOfThingsRestClient cloudOfThingsRestClient;
    protected Gson gson = GsonUtils.createGson();

    public static final String RELATIVE_API_URL = "inventory/binaries";


    public BinariesApi(CloudOfThingsRestClient cloudOfThingsRestClient) {
        this.cloudOfThingsRestClient = cloudOfThingsRestClient;
    }



    /**
     * Retrieves binaries meta data.
     *
     * @param filters the FilterBuilder to filter the retrieved binaries; null = get all
     * @param pageSize size of the results (Max. 2000); null = use default ({@link com.telekom.m2m.cot.restsdk.util.JsonArrayPagination#DEFAULT_PAGE_SIZE}
     * TODO: filters?
     * @return the pageable BinariesCollection
     */
    public BinariesCollection getBinaries(Filter.FilterBuilder filters, Integer pageSize) {
        return new BinariesCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                filters,
                pageSize);
    }


    public String uploadBinary(Binary binary) {
        byte[][] data = new byte[3][];
        String[] names = new String[3];
        names[0] = "object";
        names[1] = "filesize";
        names[2] = "file";
        data[0] = ("{\"name\":\"" + binary.getName() + "\",\n" +
                   "\"type\":\"" + binary.getType() + "\"}").getBytes();
        data[1] = (Integer.toString(binary.size())).getBytes();
        data[2] = binary.getData();
        String response = cloudOfThingsRestClient.doFormUpload(data, names, RELATIVE_API_URL);
        binary.setId(response);
        return response;
    }


    public void deleteBinary(String id) {
        cloudOfThingsRestClient.delete(id, RELATIVE_API_URL);
    }


    public void deleteBinary(Binary binary) {
        cloudOfThingsRestClient.delete(binary.getId(), RELATIVE_API_URL);
    }


    public void replaceBinary(Binary binary) {
        cloudOfThingsRestClient.doPutRequest(
                new String(binary.getData()),
                RELATIVE_API_URL + "/" + binary.getId(),
                binary.getType());
    }


    public byte[] getData(Binary binary) {
        // TODO: get name & type from response headers?
        String self = (String)binary.get("self");
        String response = cloudOfThingsRestClient.getResponse(binary.getId(), RELATIVE_API_URL, binary.getType());
        if (response != null) {
            return response.getBytes();
        } else {
            return null;
        }
    }
}
