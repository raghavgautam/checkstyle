////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.checks;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Check that banned methods are not invoked. We can ban method by name or by name & number of arguments.
 * Because of limitations of how checkstyle works we can't add checks on type of the arguments.
 * This can be used to enforce things like:
 * 1. System.exit() should not be called.
 * 2. Assert.assertTrue() has a 1 argument variant that does not provide a helpful message on failure.
 * 3. Thread.sleep() can be prohibited.
 * E.g.:
 * <p>
 * {@code
 * assertTrue(condition);
 * }
 * </p>
 *
 * @author <a href="mailto:raghavgautam@gmail.com">Raghav Kumar Gautam</a>
 */
public class BanishedMethodsCheck extends AbstractCheck {

  /**
   * A key is pointing to the warning message text in "messages.properties"
   * file.
   */
  public static final String MSG_KEY = "banished.methods";

  /**
   * Filename of banishedmethods config file.
   */
  private String file;
  private boolean optional;

  /**
   * Sets name of the config file.
   *
   * @param fileName name of the banishedmethods config file.
   */
  public void setFile(String fileName) {
    file = fileName;
  }

  /**
   * Sets whether config file existence is optional.
   *
   * @param optional tells if config file existence is optional.
   */
  public void setOptional(boolean optional) {
    this.optional = optional;
  }


  @Override
  public int[] getDefaultTokens() {
    return new int[]{
        TokenTypes.METHOD_CALL,
        TokenTypes.LITERAL_NEW,
    };
  }

  @Override
  public int[] getAcceptableTokens() {
    return getDefaultTokens();
  }

  @Override
  public int[] getRequiredTokens() {
    return getDefaultTokens();
  }

  public void setBanishedMethods(List<MethodCallInfo> banishedMethods) {
    this.banishedMethods = banishedMethods;
  }

  List<MethodCallInfo> banishedMethods = null;

  @Override
  protected void finishLocalSetup() throws CheckstyleException {
    if (file != null) {
      if (optional) {
        if (CommonUtils.configFileExists(file)) {
          banishedMethods = BanishedMethodsLoader.loadSuppressions(file);
        } else {
          banishedMethods = new ArrayList<>();
        }
      } else {
        banishedMethods = BanishedMethodsLoader.loadSuppressions(file);
      }
    }
  }

  @Override
  public void visitToken(DetailAST ast) {
    switch (ast.getType()) {
      case TokenTypes.METHOD_CALL: {
        final DetailAST dot = ast.getFirstChild();
        final String className = dot.getFirstChild().getText();
        final String methodName = dot.getLastChild().getText();
        final int numberOfParameters = dot.getNextSibling().getChildCount(TokenTypes.EXPR);
        checkBanishedMethod(ast, methodName, numberOfParameters, MethodCallInfo.of(className + "." + methodName, numberOfParameters));
        break;
      }
      case TokenTypes.LITERAL_NEW: {
        final DetailAST constructor = ast.getFirstChild();
        final String constructorName = constructor.getText();
        final int numberOfParameters = ast.findFirstToken(TokenTypes.ELIST).getChildCount(TokenTypes.EXPR);
        checkBanishedMethod(ast, constructorName, numberOfParameters, MethodCallInfo.of(constructorName, numberOfParameters));
        break;
      }
    }
  }

  private void checkBanishedMethod(DetailAST ast, String methodName, int numberOfParameters, MethodCallInfo of) {
    final boolean isBanished = banishedMethods != null &&
        banishedMethods.stream().anyMatch(
            items -> of.name.matches(items.name) && of.argCount.matches(items.argCount));
    if (isBanished) {
      log(ast.getLineNo(), ast.getColumnNo(), MSG_KEY, methodName, numberOfParameters);
    }
  }
}
