/**
 * The smartrest package holds classes to access the SmartREST interface of the cloud of things server.
 *
 * <p>
 * The starting point here is the {@link com.telekom.m2m.cot.restsdk.smartrest.SmartRestApi}, which you can get
 * from the {@link com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform}.
 * </p>
 *
 * <p>
 * To do SmartREST requests you need to store one or more
 * {@link com.telekom.m2m.cot.restsdk.smartrest.SmartRequestTemplate}s
 * and to get a SmartREST response you need to store one or more
 * {@link com.telekom.m2m.cot.restsdk.smartrest.SmartResponseTemplate}.
 * <br>
 * Then you can execute SmartREST requests and the server will use your SmartRequestTemplates to turn them into
 * regular REST requests. The JSON response of these requests will be evaluated by the SmartResponseTemplates, each
 * of which can generate one line of output, containing data that it extracted from the JSON.
 * <br>
 * All the SmartResponseTemplates are used at the same time, and each matching one will produce another response line.
 * </p>
 *
 * <p>
 * Each type or version of a device (or, more general, client) has an X-Id. X-Ids are local to a tenant.
 * This ID is used to match devices to their templates. Devices can also use/support more than one X-Id.
 * </p>
 *
 * <p>
 * Each device should be able to store it's templates, after querying the server for their existence.
 * <br>
 *     Templates cannot be updated. It is only possible to delete and recreate them all.
 * </p>
 *
 * <p>
 * Each {@link com.telekom.m2m.cot.restsdk.smartrest.SmartTemplate} has a client defined messageId, which is local
 * to the X-Id. MessageIds should start from 100, in order to not clash with server defined messages.
 * </p>
 *
 * <p>
 * For usage examples see SmartRestApiIT.
 * </p>
 *
 * <p>
 * More explanations about the concepts can be found at http://cumulocity.com/guides/reference/smartrest/
 * </p>
 */
package com.telekom.m2m.cot.restsdk.smartrest;