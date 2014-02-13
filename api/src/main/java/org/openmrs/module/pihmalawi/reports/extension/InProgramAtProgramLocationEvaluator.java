package org.openmrs.module.pihmalawi.reports.extension;

import java.util.Date;

import org.openmrs.Cohort;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.cohort.EvaluatedCohort;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.evaluator.CohortDefinitionEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;

@Handler(supports = { InProgramAtProgramLocationCohortDefinition.class })
public class InProgramAtProgramLocationEvaluator implements CohortDefinitionEvaluator {
	
	public InProgramAtProgramLocationEvaluator() {
	}
	
	public EvaluatedCohort evaluate(CohortDefinition cohortDefinition, EvaluationContext context) {
		// todo, fixme if you want
		HibernatePihMalawiQueryDao q = (HibernatePihMalawiQueryDao) Context.getRegisteredComponents(
		    HibernatePihMalawiQueryDao.class).get(0);
		InProgramAtProgramLocationCohortDefinition definition = (InProgramAtProgramLocationCohortDefinition) cohortDefinition;
		
		Date onOrAfter = definition.getOnOrAfter();
		Date onOrBefore = definition.getOnOrBefore();
		if (definition.getOnDate() != null) {
			onOrAfter = definition.getOnDate();
			onOrBefore = definition.getOnDate();
		}
		Cohort c = q.getPatientsInProgramAtLocation(definition.getPrograms(), onOrAfter, onOrBefore, definition.getLocation());
		return new EvaluatedCohort(c, cohortDefinition, context);
	}
}
