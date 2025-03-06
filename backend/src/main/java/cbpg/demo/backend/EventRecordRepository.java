package cbpg.demo.backend;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRecordRepository extends MongoRepository<EventRecordModel, String> {

}
