package cbpg.demo.backend;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadService {
    private final EventRecordRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public UploadService(EventRecordRepository eventRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public void processBatch(UploadBatch batch) {
        var user = createOrUpdateUser(batch);

        for (var record : batch.getEvents()) {
            var model = new EventRecordModel();
            model.setEvent(record);
            model.setUserId(user.getId());
            eventRepository.save(model);
        }
    }

    private UserModel createOrUpdateUser(UploadBatch batch) {
        if (batch.getPseudonyms().isEmpty()) {
            throw new RuntimeException("No pseudonyms in batch");
        }

        UserModel user = null;
        for (var pseudonym : batch.getPseudonyms()) {
            user = userRepository.findByPseudonymsContaining(pseudonym).orElse(null);
            if (user != null) {
                break;
            }
        }

        if (user == null) {
            user = new UserModel();
            user.setId(UUID.randomUUID().toString());
            user.setPseudonyms(batch.getPseudonyms());
            user = userRepository.save(user);
        } else {
            var existingPseudonyms = user.getPseudonyms();
            for (var pseudonym : batch.getPseudonyms()) {
                if (!existingPseudonyms.contains(pseudonym)) {
                    existingPseudonyms.add(pseudonym);
                }
            }

            user = userRepository.save(user);
        }

        return user;
    }
}
