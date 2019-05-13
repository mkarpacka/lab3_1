package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.application.api.handler.AddProductCommandHandler;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AddProductCommandHandlerTest {

    private AddProductCommand addProductCommand;
    private AddProductCommandHandler addProductCommandHandler;
    private ReservationRepository reservationRepository;
    private ProductRepository productRepository;
    private SuggestionService suggestionService;
    private ClientRepository clientRepository;
    private SystemContext systemContext;
    private Product product, product2;
    private Reservation reservation;
    private Client client;
    private Id id;
    private Money money;

    @Before
    public void setup() {

        id = new Id("1");
        money = new Money(3.34, Money.DEFAULT_CURRENCY);
        addProductCommand = new AddProductCommand(id, id, 5);
        product = new Product(id, money, "product1", ProductType.STANDARD);
        product2 = new Product(id, money, "product2", ProductType.STANDARD);
        reservation = new Reservation(id, Reservation.ReservationStatus.OPENED, new ClientData(id, "id"), new Date());
        systemContext = new SystemContext();

        suggestionService = mock(SuggestionService.class);
        clientRepository = mock(ClientRepository.class);
        reservationRepository = mock(ReservationRepository.class);
        productRepository = mock(ProductRepository.class);

        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product);
        when(reservationRepository.load(id)).thenReturn(reservation);
        when(productRepository.load(id)).thenReturn(product);
        when(suggestionService.suggestEquivalent(product, client)).thenReturn(product2);
        addProductCommandHandler = new AddProductCommandHandler(reservationRepository,productRepository,suggestionService,clientRepository,systemContext);
    }

    @Test
    public void testProductRepositoryLoadShouldBeCalledTwoTimes(){

        addProductCommandHandler.handle(addProductCommand);
        addProductCommandHandler.handle(addProductCommand);

        when(reservationRepository.load(id)).thenReturn(reservation);

        verify(productRepository,times(2)).load(id);

    }

    @Test
    public void testProductIsAvailableShouldReturnTrue(){

        boolean expectedResult = true;
        Assert.assertEquals(expectedResult, product.isAvailable());

    }

    @Test
    public void testProductRepositoryLoadShouldBeCalledZeroTimes() {
        verify(productRepository, times(0)).load(id);
    }








}
