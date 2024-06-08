package com.prolegacy.atom2024backend.repositories;

import com.prolegacy.atom2024backend.entities.File;
import com.prolegacy.atom2024backend.entities.ids.FileId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, FileId> {
}
