package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

class BookKeeperTest {


    @Test
    void testInvoiceRequestWithOneElementReturnOneElementInvoice() {
        Id id = Id.generate();
        ClientData client = new ClientData(id, "Magda");
        InvoiceRequest invoiceRequest = new InvoiceRequest(client);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.DRUG, new Money(10))).thenReturn(new Tax(new Money(10), "5%"));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.DRUG);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(10));
        invoiceRequest.add(requestItem);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getItems().size(), is(1));
    }

    @Test
    public void testCalculateTaxShouldBeCalledTwoTimes() {
        Id id = new Id("2");
        ClientData client = new ClientData(id, "Magda");
        InvoiceRequest invoiceRequest = new InvoiceRequest(client);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.DRUG, new Money(10))).thenReturn(new Tax(new Money(10), "5%"));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.DRUG);

        RequestItem requestItem = new RequestItem(productData, 5, new Money(10));
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(ProductType.DRUG, new Money(10));
    }

    @Test
    public void testInvoiceRequestWithZeroElementsReturnZeroElementInvoice() {
        Id id = Id.generate();
        ClientData client = new ClientData(id, "Magda");
        InvoiceRequest invoiceRequest = new InvoiceRequest(client);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.DRUG, new Money(10))).thenReturn(new Tax(new Money(10), "5%"));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.DRUG);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoiceResult.getItems().size(),is(0));
    }

    @Test
    public void testShouldReturnProperInformation() {
        Id id = Id.generate();
        ClientData client = new ClientData(id, "Magda");
        InvoiceRequest invoiceRequest = new InvoiceRequest(client);
        BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());

        TaxPolicy taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.DRUG, new Money(10))).thenReturn(new Tax(new Money(10), "5%"));

        ProductData productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.DRUG);

        Invoice invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getClient().getName(), org.hamcrest.Matchers.is(client.getName()));
        assertThat(invoiceResult.getClient().getAggregateId().getId(), is(id.getId()));
    }

}
