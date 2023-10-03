package dev.lukasnehrke.vulnaware.project.repository;

import dev.lukasnehrke.vulnaware.project.model.ProjectMember;
import dev.lukasnehrke.vulnaware.project.model.ProjectMemberId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends CrudRepository<ProjectMember, ProjectMemberId> {}
