package org.oscartrugo.springcloud.msvc.usuarios.msvc_usuarios.controllers;

import jakarta.validation.Valid;
import org.oscartrugo.springcloud.msvc.usuarios.msvc_usuarios.models.entity.Usuario;
import org.oscartrugo.springcloud.msvc.usuarios.msvc_usuarios.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable("id") Long id) {
        Optional<Usuario> usuarioOptional = service.porId(id);
        if(usuarioOptional.isPresent()) {
            return ResponseEntity.ok(usuarioOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result) {
        if(service.porEmail(usuario.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("mensaje", "Ya existe un usuario con este email"));
        }
        if(result.hasErrors()) { //Si el result tiene errores
            return validar(result);

        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.guardar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable("id") Long id) {

        if(result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> o = service.porId(id);
        if(o.isPresent()) {
            Usuario usuarioDB = o.get();
            if(!usuario.getEmail().equals(usuarioDB.getEmail()) && service.porEmail(usuario.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("mensaje", "Ya existe un usuario con este email"));
            }
            usuarioDB.setNombre(usuario.getNombre());
            usuarioDB.setEmail(usuario.getEmail());
            usuarioDB.setPassword(usuario.getPassword());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(service.guardar(usuarioDB));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Optional<Usuario> o = service.porId(id);
        if(o.isPresent()) {
            service.eliminar(id);
            return ResponseEntity
                    .noContent()
                    .build();
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