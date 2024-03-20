package me.jellysquid.mods.sodium.client.gui.vanilla.builders;

import me.jellysquid.mods.sodium.client.gui.SodiumGameOptions;
import me.jellysquid.mods.sodium.client.gui.vanilla.option.CyclingOption;
import me.jellysquid.mods.sodium.client.gui.vanilla.options.IndexedOption;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.apache.commons.lang3.Validate;

import java.util.function.BiFunction;

public class CycleOptionBuilder<V extends IndexedOption> extends OptionBuilder<CycleOptionBuilder<V>, CyclingOption<V>, V> {

    private V[] options;

    @Override
    CycleOptionBuilder<V> self() {
        return this;
    }

    public CycleOptionBuilder<V> setOptions(V[] options){
        this.options = options;
        return self();
    }

    V[] getOptions(){
        Validate.notNull(options, "Options must be specified");
        return options;
    }

    @Override
    public CyclingOption<V> build() {
        String text = this.getText();
        BiFunction<SodiumGameOptions, CyclingOption<V>, Text> textGetter = getTextGetter();
        return new CyclingOption<>(
                getKey(),
                sodiumOpts.getData(),
                getOptions(),
                getSetter(),
                getGetter(),
                text == null?
                        (options, self) -> self.getDisplayPrefix().shallowCopy().append(textGetter.apply(options, self))
                        :
                        (options, self) -> new LiteralText(text).append(": ").append(textGetter.apply(options, self))
        );
    }
}
