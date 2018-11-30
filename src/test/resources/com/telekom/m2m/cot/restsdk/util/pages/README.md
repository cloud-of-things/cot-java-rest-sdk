# Pages #

This directory contains some paged example responses from the Cumulocity REST API.

Additionally, it contains a template file `page.json.template` that is used to create mock responses in tests.

Observations: 

- When filters are applied, then no total number of pages is available.
- When filters are applied, then the response contains *always* a `next` attribute, even if there is no next page.