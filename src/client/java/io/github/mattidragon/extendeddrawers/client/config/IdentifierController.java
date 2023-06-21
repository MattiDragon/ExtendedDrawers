package io.github.mattidragon.extendeddrawers.client.config;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.controllers.string.IStringController;
import net.minecraft.util.Identifier;

public record IdentifierController(Option<Identifier> option) implements IStringController<Identifier> {
    @Override
    public String getString() {
        return option.pendingValue().toString();
    }

    @Override
    public void setFromString(String value) {
        option.requestSet(Identifier.tryParse(value));
    }

    @Override
    public boolean isInputValid(String input) {
        return Identifier.isValid(input);
    }
}
