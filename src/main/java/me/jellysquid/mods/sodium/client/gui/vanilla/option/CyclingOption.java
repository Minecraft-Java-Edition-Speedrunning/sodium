package me.jellysquid.mods.sodium.client.gui.vanilla.option;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.gui.vanilla.options.IndexedOption;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.OptionButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CyclingOption<V extends IndexedOption> extends Option {

    int index;
    SodiumGameOptions gameOptions;
    V[] options;
    BiConsumer<SodiumGameOptions, V> setter;
    Function<SodiumGameOptions, V> getter;
    BiFunction<SodiumGameOptions, CyclingOption<V>, String> displayStringGetter;

    public CyclingOption(String key, SodiumGameOptions gameOptions, V[] options, BiConsumer<SodiumGameOptions, V> setter, Function<SodiumGameOptions, V> getter, BiFunction<SodiumGameOptions, CyclingOption<V>, String> displayStringGetter) {
        super(key);
        this.gameOptions = gameOptions;
        this.options = options;
        this.setter = setter;
        this.getter = getter;
        this.displayStringGetter = displayStringGetter;
    }

    private String getText(){
        return this.displayStringGetter.apply(gameOptions, this);
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width) {
        String message = getText();
        index = getter.apply(gameOptions).getIndex();
        return new OptionButtonWidget(x, y, width, 20, this, message, (button) -> {
            int next_index = index + 1;
            if(next_index == this.options.length){
                next_index = 0;
            }
            setter.accept(gameOptions, this.options[next_index]);
            index = getter.apply(gameOptions).getIndex();
            button.setMessage(this.getText());
        });
    }
}