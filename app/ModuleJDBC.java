import com.google.inject.AbstractModule;
import services.IShortService;
import services.ShortService;
import services.ShortServiceJDBC;

/**
 * Created by vnazarov on 28/02/17.
 */
public class ModuleJDBC extends AbstractModule{
    @Override
    protected void configure() {
        bind(IShortService.class)
                .to(ShortServiceJDBC.class)
                .asEagerSingleton();
    }
}
