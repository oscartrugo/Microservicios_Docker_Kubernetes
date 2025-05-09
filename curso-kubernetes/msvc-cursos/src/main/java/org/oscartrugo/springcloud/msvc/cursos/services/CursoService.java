package org.oscartrugo.springcloud.msvc.cursos.services;

import org.oscartrugo.springcloud.msvc.cursos.models.entity.Curso;
import org.oscartrugo.springcloud.msvc.cursos.models.dto.Usuario;

import java.util.List;
import java.util.Optional;

public interface CursoService {
    List<Curso> listar();
    Optional<Curso> porId(Long id);
    Curso guardar(Curso curso);
    void eliminar(Long id);
    Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId);
    Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId);
}