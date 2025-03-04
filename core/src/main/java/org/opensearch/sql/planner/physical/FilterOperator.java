/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.sql.planner.physical;

import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.opensearch.sql.data.model.ExprValue;
import org.opensearch.sql.expression.Expression;
import org.opensearch.sql.expression.operator.predicate.BinaryPredicateOperator;
import org.opensearch.sql.storage.bindingtuple.BindingTuple;

/**
 * The Filter operator represents WHERE clause and
 * uses the conditions to evaluate the input {@link BindingTuple}.
 * The Filter operator only returns the results that evaluated to true.
 * The NULL and MISSING are handled by the logic defined in {@link BinaryPredicateOperator}.
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@RequiredArgsConstructor
public class FilterOperator extends PhysicalPlan {
  @Getter
  private final PhysicalPlan input;
  @Getter
  private final Expression conditions;
  @ToString.Exclude
  private ExprValue next = null;

  @Override
  public <R, C> R accept(PhysicalPlanNodeVisitor<R, C> visitor, C context) {
    return visitor.visitFilter(this, context);
  }

  @Override
  public List<PhysicalPlan> getChild() {
    return Collections.singletonList(input);
  }

  @Override
  public boolean hasNext() {
    while (input.hasNext()) {
      ExprValue inputValue = input.next();
      ExprValue exprValue = conditions.valueOf(inputValue.bindingTuples());
      if (!(exprValue.isNull() || exprValue.isMissing()) && (exprValue.booleanValue())) {
        next = inputValue;
        return true;
      }
    }
    return false;
  }

  @Override
  public ExprValue next() {
    return next;
  }
}
