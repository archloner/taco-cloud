package sia.tacos.data;

import org.springframework.data.repository.CrudRepository;
import sia.tacos.model.Taco;

public interface TacoRepository extends CrudRepository<Taco, Long> {
}
