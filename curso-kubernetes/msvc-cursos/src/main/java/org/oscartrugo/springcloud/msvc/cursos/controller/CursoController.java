package org.oscartrugo.springcloud.msvc.cursos.controller;

import feign.FeignException;
import jakarta.validation.Valid;
import org.oscartrugo.springcloud.msvc.cursos.models.dto.Usuario;
import org.oscartrugo.springcloud.msvc.cursos.models.entity.Curso;
import org.oscartrugo.springcloud.msvc.cursos.services.CursoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class CursoController {

    @Autowired
    private CursoService service;

    private static final Logger log = LoggerFactory.getLogger(CursoController.class);

    @GetMapping
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Optional<Curso> o = service.porId(id);
        if(o.isPresent()) {
            return ResponseEntity.ok(o.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Curso curso, BindingResult result) {
        if(result.hasErrors()) {
            return validar(result);
        }
        Curso cursoDB = service.guardar(curso);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoDB);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, BindingResult result, @PathVariable Long id) {
        if(result.hasErrors()) {
            return validar(result);
        }
        Optional<Curso> o = service.porId(id);
        if(o.isPresent()) {
            Curso cursoDb = o.get();
            cursoDb.setNombre(curso.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Curso> o = service.porId(id);
        if(o.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/asignar-usuario/{cursoId}")
    public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        Optional<Usuario> optionalUsuario;
        try {
            optionalUsuario = service.asignarUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", "No existe el usuario: " + e.getMessage()));
        }
        if (optionalUsuario.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(optionalUsuario.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crear-usuario/{cursoId}")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        Optional<Usuario> optionalUsuario;
        try {
            optionalUsuario = service.crearUsuario(usuario, cursoId);
        } catch (FeignException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", "No se pudo crear el usuario debido a un error en la comunicación: " + e.getMessage()));
        }
        if (optionalUsuario.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(optionalUsuario.get());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar-usuario/{cursoId}")
    public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
        Optional<Usuario> optionalUsuario;
        try {
            optionalUsuario = service.eliminarUsuario(usuario, cursoId);
        } catch (FeignException e) {
            log.error("Error al intentar eliminar un usuario de un curso: " + e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", "No existe el usuario: " + e.getMessage()));
        }
        if (optionalUsuario.isPresent()) {
            log.info("Usuario eliminado correctamente: " + optionalUsuario.get().getNombre());
            return ResponseEntity.status(HttpStatus.OK).body(optionalUsuario.get());
        }
        return ResponseEntity.notFound().build();
    }

    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(e -> {
            errores.put(e.getField(), "El campo " + e.getField() + " " + e.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errores);
    }

}
