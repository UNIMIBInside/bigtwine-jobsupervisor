package it.unimib.disco.bigtwine.services.jobsupervisor.repository;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
}
