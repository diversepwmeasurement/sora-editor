/*
 *    CodeEditor - the awesome code editor for Android
 *    Copyright (C) 2020-2021  Rosemoe
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
package io.github.rosemoe.sora.langs.textmate;

import java.io.InputStream;

import io.github.rosemoe.sora.interfaces.AutoCompleteProvider;
import io.github.rosemoe.sora.interfaces.CodeAnalyzer;
import io.github.rosemoe.sora.interfaces.EditorLanguage;
import io.github.rosemoe.sora.interfaces.NewlineHandler;
import io.github.rosemoe.sora.langs.EmptyLanguage;
import io.github.rosemoe.sora.textmate.core.grammar.IGrammar;
import io.github.rosemoe.sora.textmate.core.theme.IRawTheme;
import io.github.rosemoe.sora.widget.SymbolPairMatch;

public class TextMateLanguage extends EmptyLanguage{

    private TextMateAnalyzer textMateAnalyzer;

    private TextMateLanguage(String grammarName,InputStream grammarIns, IRawTheme theme){
        try {
            textMateAnalyzer=new TextMateAnalyzer(grammarName,grammarIns, theme);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * When you update the {@link TextMateColorScheme} for editor, you need to synchronize the updates here
     * @param theme IRawTheme creates from file
     */

    public void updateTheme(IRawTheme theme){
        if(textMateAnalyzer!=null){
            textMateAnalyzer.updateTheme(theme);
        }
    }
    public static TextMateLanguage create(String grammarName,InputStream grammarIns, IRawTheme theme) {
        return new TextMateLanguage(grammarName, grammarIns, theme);
    }

    @Override
    public CodeAnalyzer getAnalyzer(){
        if(textMateAnalyzer!=null){
            return textMateAnalyzer;
        }
        return super.getAnalyzer();
    }
}
