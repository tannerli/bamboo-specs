package bamboo;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.artifact.Artifact;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.MavenTask;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;

@BambooSpec
public class PlanSpec {

  public static final String PROJECT_KEY = "MS";
  public static final String PLAN_KEY = "VS";

  public Plan plan() {
    VcsCheckoutTask checkoutDefaultRepository = new VcsCheckoutTask()
        .description("Checkout Default Repository")
        .checkoutItems(new CheckoutItem().defaultRepository());

    Project project = new Project()
        .key(new BambooKey(PROJECT_KEY))
        .name("MTA Spielwiese");

    return new Plan(project,
        "Plan via Specs",
        new BambooKey(PLAN_KEY))
        .stages(
            new Stage("Default Stage")
            .jobs(new Job("Default Job",
                new BambooKey("JOB1"))
                .tasks(
                    checkoutDefaultRepository,
                    new MavenTask()
                        .executableLabel("Maven 3")
                        .goal("test package")
                        .hasTests(true)
                ).artifacts(new Artifact()
                    .name("Acme™ Application")
                        .copyPatterns("target/acme-*.jar"),
                    new Artifact("test")
                        .copyPatterns("*.txt")
                )))
        .linkedRepositories("mta-test")
        .triggers(new RepositoryPollingTrigger())

        .planBranchManagement(new PlanBranchManagement()
            .delete(new BranchCleanup())
            .notificationForCommitters());
  }

  public PlanPermissions planPermission() {
    return new PlanPermissions(new PlanIdentifier(PROJECT_KEY, PLAN_KEY))
        .permissions(new Permissions().
            userPermissions("bamboo", PermissionType.VIEW, PermissionType.EDIT, PermissionType.BUILD,
                PermissionType.CLONE, PermissionType.ADMIN, PermissionType.VIEW_CONFIGURATION,
                PermissionType.CREATE_PLAN_BRANCH));
  }

  public static void main(String... argv) {
    BambooServer bambooServer = new BambooServer("https://bamboo.emhartglass.net");
    final PlanSpec planSpec = new PlanSpec();

    final Plan plan = planSpec.plan();
    bambooServer.publish(plan);

    final PlanPermissions planPermission = planSpec.planPermission();
    bambooServer.publish(planPermission);
  }
}