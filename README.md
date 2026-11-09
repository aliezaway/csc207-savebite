# SaveBite

A CSC207 team project for managing food purchases, date reminders and food waste.

## Team

| Member | Planned feature ownership |
|---|---|
| Xiangyu Zhou | Record purchases, create products with barcode lookup, inventory and date reminders |
| Kenny Gu | Edit/archive products, mark batch outcomes, spending reports |

Xiangyu also leads storage, API integration and application assembly. Both members review shared contracts and each other's PRs.

## Current version: product creation, purchases and batch outcomes

- Shared product and purchase entities, data access interfaces, and in-memory storage.
- UC2 creates products manually or fills in their name and brand through Open Food Facts barcode lookup.
- UC1 records purchase batches, validates inputs, and supports confirmed target dates.
- UC5 marks a whole batch as eaten or discarded and prevents repeated processing.

The product and purchase dialogs and the batch outcome panel will be connected to the main window during integration. In-memory data is lost when the process ends. JSON persistence is planned.

## Core rules

- A product describes an item; a purchase represents one batch.
- Batches are marked eaten or discarded as a whole.
- Passing a target date does not automatically discard a batch.
- Estimated dates are reminders, not food safety guarantees.
- Purchase prices represent batch totals in CAD.

## Requirements and tests

Install Java 17 or newer and Maven. From the project root:

```bash
mvn test
```

This compiles the foundation code and runs the tests. The current version has no `Main` class; `mvn exec:java` is not available yet.

## Project structure

```text
docs/                        Development agreement and API contracts
src/main/java/savebite/
  entity/                    Products, purchase batches and enums
  use_case/                  Shared storage and lookup contracts
  data_access/               In-memory implementations
src/test/java/savebite/       Entity tests and shared test data
pom.xml                      Java version, dependencies and build configuration
```

## Development guide

See [Development Agreement and API Contracts](docs/development-agreement.md) for feature ownership, interface names and the planned workflow. Exact signatures and behaviour are documented in the Java interfaces. Develop against these interfaces using in-memory storage while persistent storage is unfinished.

## Planned MVP

1. Create products manually or retrieve details through Open Food Facts.
2. Record purchase batches and confirmed target dates.
3. View inventory and date reminders.
4. Edit and archive products.
5. Mark whole batches eaten or discarded.
6. Report spending and discarded value, with JSON storage across restarts.

External API: [Open Food Facts documentation](https://openfoodfacts.github.io/openfoodfacts-server/api/). The gateway calls the service when the product dialog requests a lookup. Manual entry is available if lookup fails.
