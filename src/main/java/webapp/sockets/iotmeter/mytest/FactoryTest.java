package webapp.sockets.iotmeter.mytest;

/**
 * Created by Administrator on 2017/3/16.
 */
public class FactoryTest {
    public interface IProduct{
        public void productMethod();
    }

    static class Product implements IProduct{

        @Override
        public void productMethod() {
            System.out.println("产品");
        }
    }

    public interface IFactory{
        public IProduct createProduct();
    }

    static class HeheFactory implements IFactory{

        @Override
        public IProduct createProduct() {
            return new Product();
        }
    }

    public static class Client{
        public static void main(String[] args){
            IFactory factory = new HeheFactory();
            IProduct product = factory.createProduct();
            product.productMethod();
        }
    }
}

