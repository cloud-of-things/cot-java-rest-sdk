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
     * @param pageSize size of the result page (Max. 2000); null = use default ({@link com.telekom.m2m.cot.restsdk.util.JsonArrayPagination#DEFAULT_PAGE_SIZE}
     * TODO: filters?
     * @return the pageable BinariesCollection
     */
    public BinariesCollection getBinaries(Integer pageSize) {
        return new BinariesCollection(
                cloudOfThingsRestClient,
                RELATIVE_API_URL,
                gson,
                pageSize);
    }


    /**
     * Upload/create a new Binary.
     *
     * @param binary the new Binary, with name, type and data. Will receive the ID of the newly created resource.
     * @return the ID of the newly created resource.
     */
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


    /**
     * Delete a Binary from the CoT.
     *
     * @param id the ID of the Binary that shall be deleted.
     */
    public void deleteBinary(String id) {
        cloudOfThingsRestClient.delete(id, RELATIVE_API_URL);
    }


    /**
     * Delete a Binary from the CoT.
     *
     * @param binary the Binary to delete (by its ID).
     */
    public void deleteBinary(Binary binary) {
        cloudOfThingsRestClient.delete(binary.getId(), RELATIVE_API_URL);
    }


    /**
     * Replace the data of the binary in the CoT.
     * This will result in a new ID for the binary. The old ID will stop existing.
     *
     * @param binary the Binary that has the (old) ID and the (new) data. The ID will be updated.
     * @return the new ID of the binary.
     */
    public String replaceBinary(Binary binary) {
        String newId = cloudOfThingsRestClient.doPutRequestWithIdResponse(
                new String(binary.getData()),
                RELATIVE_API_URL + "/" + binary.getId(),
                binary.getType());
        binary.setId(newId);
        return newId;
    }


    /**
     * Get (or "fill in") the actual binary data into a Binary (e.g. from a {@link BinariesCollection}).
     * This data is identified by the ID of the Binary (and not the self-link (although that would probably work too)).
     *
     * @param binary the Binary for which the data shall be downloaded. Will be enriched with that data.
     * @return the original byte[] from the response or null, if the body is empty.
     */
    public byte[] getData(Binary binary) {
        String responseBody = cloudOfThingsRestClient.getResponse(binary.getId(), RELATIVE_API_URL, binary.getType());
        if (responseBody != null) {
            byte[] data = responseBody.getBytes();
            binary.setData(data);
            return data;
        } else {
            return null;
        }
    }
}
