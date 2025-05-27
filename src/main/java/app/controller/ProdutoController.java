package app.controller;

import java.util.List;

import app.entity.Produto;
import app.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<Produto> save(@RequestBody Produto produto) {
        Produto novoProduto = produtoService.save(produto);
        return new ResponseEntity<>(novoProduto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<Produto> update(@PathVariable("id") long id, @RequestBody Produto produto) {
        produto.setId(id);
        Produto produtoAtualizado = produtoService.save(produto);
        return new ResponseEntity<>(produtoAtualizado, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listAll")
    public ResponseEntity<List<Produto>> listAll() {
        List<Produto> produtos = produtoService.listAll();
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findById/{id}")
    public ResponseEntity<Produto> findById(@PathVariable("id") long id) {
        return produtoService.findById(id)
                .map(produto -> new ResponseEntity<>(produto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) {
        produtoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findByNome/{nome}")
    public ResponseEntity<List<Produto>> findByNome(@PathVariable("nome") String nome) {
        List<Produto> produtos = produtoService.findByNome(nome);
        if (produtos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(produtos, HttpStatus.OK);
    }
}
