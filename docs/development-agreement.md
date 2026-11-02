# SaveBite — Development Agreement

**Team:** Xiangyu Zhou and Kenny Gu  
**Status:** Draft for team review · 2026-11-02

## 1. Features and ownership

| Xiangyu Zhou | Kenny Gu |
|---|---|
| Record purchases | Edit and archive products |
| Create products, including barcode lookup | Mark whole batches eaten or discarded |
| Show inventory and date reminders | Report spending and discarded value |
| JSON storage, external API and app assembly | UI connections and tests for his features |

Each member owns the business logic, controller, presenter, UI interaction and tests for their features. Agree on shared entities and interfaces together; review each other’s PRs.

**Not in the MVP:** recipes, partial consumption, camera scanning, charts and undo.

## 2. Shared rules

- **Product** is reusable item information; **Purchase** is one purchase batch.
- Process each batch as a whole. Passing its target date does not automatically discard it.
- Prices are batch totals in CAD. Quantity must be positive.
- Estimated dates require user confirmation. Editing default days does not change existing batches.
- Block archiving while stock remains; keep historical records.
- Spending uses purchase date; discarded value uses outcome date. Date ranges include both endpoints.
- Failed saves must leave the previous data unchanged and show an error.

## 3. APIs we agree to use

These are Java interfaces. The source paths below are planned locations; the Java files will be added during implementation. Use those files for exact parameter types and Javadoc once available.

| Interface | Methods | Contract |
|---|---|---|
| `ProductDataAccessInterface` | findById, findByBarcode, listAll, save | Lists include archived products. Save inserts or updates by ID. |
| `PurchaseDataAccessInterface` | findById, listAll, save | Lists include every status. Save inserts or updates by ID. |
| `ProductLookupGateway` | lookup | Returns product name/brand from a barcode; does not save it. Either field may be missing. |

Find/lookup methods return `Optional.empty()` when nothing is found. Storage failures use `DataAccessException`; lookup failures use `ProductLookupException`.

Use `InMemoryProductDataAccess` and `InMemoryPurchaseDataAccess` while JSON storage is unfinished. Use a fake `ProductLookupGateway` while the real API is unfinished. Callers can develop against interfaces before the real implementations are ready.

### Feature entry points

All are under `src/main/java/savebite/use_case/`. Results go through the matching `OutputBoundary` to a presenter.

| Input interface | Methods |
|---|---|
| RecordPurchaseInputBoundary | loadProducts(); suggestTargetDate(productId, purchaseDateText); execute(RecordPurchaseInputData) |
| CreateProductInputBoundary | lookup(barcode); execute(CreateProductInputData) |
| ViewInventoryInputBoundary | execute() |
| EditProductInputBoundary | search(query, includeArchived); execute(EditProductInputData); archive(productId) |
| MarkPurchaseOutcomeInputBoundary | execute(MarkPurchaseOutcomeInputData) |
| SpendingReportInputBoundary | execute(SpendingReportInputData) |

During implementation, define the fields in the corresponding `InputData` class. Do not rename shared methods without discussing the change and updating callers, implementations and tests.

## 4. Development and PR order

1. **Together:** confirm rules, entities and interface contracts; Xiangyu prepares foundations, Kenny reviews.
2. **In parallel:** Xiangyu develops purchase recording; Kenny develops batch outcomes.
3. **In parallel:** Xiangyu develops product creation and inventory; Kenny develops product editing and reports.
4. **Integration:** Xiangyu connects JSON and Open Food Facts; both connect UI updates and verify complete flows. Test a real API lookup early.
5. **Finish:** fix actual bugs, run tests, update README and rehearse the demo.

Use `feature/<name>` branches and small PRs reviewed by the other member. For a real bug, use `fix/<name>`, describe reproduction steps and verify the fix with a regression test where useful. Each member commits their own actual work; this plan is not a record of past authorship.

## 5. Keep the design simple

**Flow:** View → Controller → Interactor → OutputBoundary/Presenter → ViewModel → View.

- **S:** separate UI, business rules and storage/network work.
- **O / D:** depend on interfaces so implementations can be replaced.
- **L:** real and temporary implementations honour the same contracts.
- **I:** keep product storage, purchase storage and lookup interfaces separate.

Use ordinary classes and clear methods. Do not add patterns without a concrete need.

**Done means:** acceptance rules and error paths work, relevant tests pass, the other member reviews, and documentation matches the code. Both verify create → purchase → reminder → edit → outcome → report → restart.
