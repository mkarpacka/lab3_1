package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
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

    private Id id;
    private ClientData client;
    private InvoiceRequest invoiceRequest;
    private BookKeeper bookKeeper;
    private TaxPolicy taxPolicy;
    private ProductData productData;
    private RequestItem requestItem;
    private Invoice invoiceResult;

    @BeforeEach
    public void setup() {
        id = Id.generate();
        client = new ClientData(id, "Magda");
        invoiceRequest = new InvoiceRequest(client);
        bookKeeper = new BookKeeper(new InvoiceFactory());

        taxPolicy = mock(TaxPolicy.class);
        when(taxPolicy.calculateTax(ProductType.DRUG, new Money(10))).thenReturn(new Tax(new Money(10), "5%"));

        productData = mock(ProductData.class);
        when(productData.getType()).thenReturn(ProductType.DRUG);

        requestItem = new RequestItem(productData, 5, new Money(10));
    }

    @Test
    public void testInvoiceRequestWithOneElementReturnOneElementInvoice() {

        invoiceRequest.add(requestItem);

        invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getItems().size(), is(1));
    }

    @Test
    public void testCalculateTaxShouldBeCalledTwoTimes() {

        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(ProductType.DRUG, new Money(10));
    }

    @Test
    public void testInvoiceRequestWithZeroElementsReturnZeroElementInvoice() {

        invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoiceResult.getItems().size(),is(0));
    }

    @Test
    public void testShouldReturnProperInformation() {
        invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getClient().getName(), is(client.getName()));
        assertThat(invoiceResult.getClient().getAggregateId().getId(), is(id.getId()));
    }

    @Test
    public void testShouldReturnFoodAsProductTypeOfThirdItem() {

        RequestItem requestItem1 = new RequestItem(productData, 5, new Money(10));
        RequestItem requestItem2 = new RequestItem(productData, 5, new Money(10));
        RequestItem requestItem3 = new RequestItem(productData, 5, new Money(10));

        invoiceRequest.add(requestItem1);
        invoiceRequest.add(requestItem2);
        invoiceRequest.add(requestItem3);

        invoiceResult = bookKeeper.issuance(invoiceRequest, taxPolicy);

        assertThat(invoiceResult.getItems().get(2).getProduct().getType(), is(ProductType.DRUG));
    }

    @Test public void testCalculateTaxBeCalledZeroTimes() {

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(0)).calculateTax(ProductType.DRUG, new Money(10));
    }

}
