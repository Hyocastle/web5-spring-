package net.datasa.web5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.datasa.web5.domain.entity.ReplyEntity;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

}
