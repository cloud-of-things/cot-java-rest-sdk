/**
 * Retention rules control how long different types of information are kept on the cloud of things server.
 *
 * <p>
 * The starting point here is the {@link com.telekom.m2m.cot.restsdk.retentionrule.RetentionRuleApi}, which you can get
 * from the {@link com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform}.
 * </p>
 *
 * <p>
 * The retention time is measured in days and can be set individually for the different data
 * types (EVENT, MEASUREMENT, ALARM, AUDIT, OPERATION) or for everything (*).
 * Depending on the data type additional filtering by type, fragment type and source is possible.
 * </p>
 *
 * <p>
 * More explanations about the concepts can be found at http://cumulocity.com/guides/reference/retention-rules/
 * </p>
 */
package com.telekom.m2m.cot.restsdk.retentionrule;