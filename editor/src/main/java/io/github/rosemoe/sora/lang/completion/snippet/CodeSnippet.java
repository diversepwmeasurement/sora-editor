/*
 *    sora-editor - the awesome code editor for Android
 *    https://github.com/Rosemoe/sora-editor
 *    Copyright (C) 2020-2022  Rosemoe
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 *
 *     Please contact Rosemoe by email 2073412493@qq.com if you need
 *     additional information or have any questions
 */
package io.github.rosemoe.sora.lang.completion.snippet;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class CodeSnippet implements Cloneable {

    private final List<SnippetItem> items;
    private final List<PlaceholderDefinition> placeholders;

    public CodeSnippet(@NonNull List<SnippetItem> items, @NonNull List<PlaceholderDefinition> placeholders) {
        this.items = items;
        this.placeholders = placeholders;
    }

    public boolean checkContent() {
        int index = 0;
        int end = 0;
        for (var item : items) {
            if (item.getStartIndex() != index) {
                return false;
            }
            if (item instanceof PlaceholderItem) {
                if (!placeholders.contains(((PlaceholderItem) item).getDefinition()) || ((PlaceholderItem) item).getText().contains("\n") || ((PlaceholderItem) item).getText().contains("\r")) {
                    return false;
                }
            }
            if (item instanceof SelectionEndItem) {
                end ++;
                if (end > 1) {
                    return false;
                }
            }
            index = item.getEndIndex();
        }
        var set = new TreeSet<String>();
        for (var placeholder : placeholders) {
            if (!set.contains(placeholder.getId())) {
                set.add(placeholder.getId());
            } else {
                return false;
            }
        }
        return true;
    }

    public List<SnippetItem> getItems() {
        return items;
    }

    public List<PlaceholderDefinition> getPlaceholderDefinitions() {
        return placeholders;
    }

    @NonNull
    @Override
    public CodeSnippet clone() {
        var itemsClone = new ArrayList<SnippetItem>(items.size());
        for (SnippetItem item : items) {
            itemsClone.add(item.clone());
        }
        var defs = new ArrayList<PlaceholderDefinition>(placeholders.size());
        for (PlaceholderDefinition placeholder : placeholders) {
            defs.add(new PlaceholderDefinition(placeholder.getId(), placeholder.getDefaultValue()));
        }
        return new CodeSnippet(itemsClone, defs);
    }

    public static class Builder {

        private final List<PlaceholderDefinition> definitions;
        private List<SnippetItem> items = new ArrayList<>();
        private int index;

        public Builder() {
            this(new ArrayList<>());
        }

        public Builder(@NonNull List<PlaceholderDefinition> definitions) {
            this.definitions = definitions;
        }

        public Builder addPlainText(String text) {
            if (!items.isEmpty() && items.get(items.size() - 1) instanceof PlainTextItem) {
                // Merge plain texts
                var item = (PlainTextItem) items.get(items.size() - 1);
                item.setText(item.getText() + text);
                item.setIndex(item.getStartIndex(), item.getEndIndex() + text.length());
                index += text.length();
                return this;
            }
            items.add(new PlainTextItem(text, index));
            index += text.length();
            return this;
        }

        public Builder addPlaceholder(String id) {
            PlaceholderDefinition def = null;
            for (var definition : definitions) {
                if (definition.getId().equals(id)) {
                    def = definition;
                    break;
                }
            }
            if (def == null) {
                def = new PlaceholderDefinition(id, id);
                definitions.add(def);
            }
            var item = new PlaceholderItem(def, index);
            item.setText(def.getDefaultValue());
            items.add(item);
            index += def.getDefaultValue().length();
            return this;
        }

        public Builder addSelectedText() {
            items.add(new SelectedTextItem(index));
            return this;
        }

        public Builder addSelectionEnd() {
            items.add(new SelectionEndItem(index));
            return this;
        }

        public Builder addTabStop(int number) {
            items.add(new TabStopItem(index, number));
            return this;
        }

        public CodeSnippet build() {
            return new CodeSnippet(items, definitions);
        }

    }

}
