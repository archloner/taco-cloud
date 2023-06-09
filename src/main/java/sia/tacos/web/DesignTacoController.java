package sia.tacos.web;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import sia.tacos.data.IngredientsRepository;
import sia.tacos.data.TacoRepository;
import sia.tacos.model.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sia.tacos.model.Ingredient.Type;
import sia.tacos.model.Order;
import sia.tacos.model.Taco;


@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientsRepository ingredientsRepository;
    private TacoRepository designRepo;

    @Autowired
    public DesignTacoController(IngredientsRepository ingredientsRepository,
                                TacoRepository designRepo) {
        this.ingredientsRepository = ingredientsRepository;
        this.designRepo = designRepo;
    }

    @GetMapping
    public String showDesignForm(Model model) {
        addIngredientsList(model);
//        model.addAttribute("design", new Taco());
        return "design";
    }

    private Model addIngredientsList(Model model) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientsRepository.findAll().forEach(ingredients::add);

        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }

        return model;
    }

    private List<Object> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients.stream()
                .filter(i -> type.equals(i.getType()))
                .collect(Collectors.toList());
    }

    @ModelAttribute("order")
    public Order order() {
        return new Order();
    }

    @ModelAttribute("design")
    public Taco taco() {
        return new Taco();
    }

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors, Model model, @ModelAttribute Order order) {
        if (errors.hasErrors()) {
            log.error("Design form has validation errors: {}", errors.getAllErrors());
            addIngredientsList(model);
            return "design";
        }

        log.info("Przetwarzanie projektu taco: {}...", design);
        Taco saved = designRepo.save(design);
        order.addDesign(saved);

        return "redirect:/orders/current";
    }

}
