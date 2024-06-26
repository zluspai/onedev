package io.onedev.server.buildspec.step;

import io.onedev.k8shelper.PruneBuilderCacheFacade;
import io.onedev.k8shelper.StepFacade;
import io.onedev.server.annotation.*;
import io.onedev.server.buildspec.param.ParamCombination;
import io.onedev.server.model.Build;
import io.onedev.server.model.support.administration.jobexecutor.JobExecutor;

import static io.onedev.server.buildspec.step.StepGroup.DOCKER_IMAGE;

@Editable(order=260, name="Prune Builder Cache", group = DOCKER_IMAGE, description="" +
		"Prune image cache of docker buildx builder. This step calls docker builder prune command " +
		"to remove cache of buildx builder specified in server docker executor or remote docker executor")
public class PruneBuilderCacheStep extends Step {

	private static final long serialVersionUID = 1L;
	
	private String options;

	@Editable(order=100, description = "Optionally specify options for docker builder prune command")
	@ReservedOptions({"-f", "--force", "--builder"})
	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	@Override
	public StepFacade getFacade(Build build, JobExecutor jobExecutor, String jobToken, ParamCombination paramCombination) {
		return new PruneBuilderCacheFacade(getOptions());
	}
	
}
