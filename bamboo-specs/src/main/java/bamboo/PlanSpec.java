package bamboo;

import com.atlassian.bamboo.specs.api.builders.BambooKey;
import com.atlassian.bamboo.specs.api.builders.BambooOid;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.plan.branches.BranchCleanup;
import com.atlassian.bamboo.specs.api.builders.plan.branches.PlanBranchManagement;
import com.atlassian.bamboo.specs.api.builders.plan.configuration.ConcurrentBuilds;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.builders.task.CheckoutItem;
import com.atlassian.bamboo.specs.builders.task.VcsCheckoutTask;
import com.atlassian.bamboo.specs.builders.trigger.RepositoryPollingTrigger;
import com.atlassian.bamboo.specs.util.BambooServer;

public class PlanSpec {

  public Plan plan() {
    final Plan plan = new Plan(new Project()
        .oid(new BambooOid("1l2xac5plwzcx"))
        .key(new BambooKey("MS"))
        .name("MTA Spielwiese"),
        "Erster Build",
        new BambooKey("EB"))
        .oid(new BambooOid("1l2nl4kce3669"))
        .enabled(false)
        .pluginConfigurations(new ConcurrentBuilds())
        .stages(new Stage("Default Stage")
            .jobs(new Job("Default Job",
                new BambooKey("JOB1"))
                .tasks(new VcsCheckoutTask()
                    .description("Checkout Default Repository")
                    .checkoutItems(new CheckoutItem().defaultRepository()))))
        .linkedRepositories("mta-test")

        .triggers(new RepositoryPollingTrigger())
        .planBranchManagement(new PlanBranchManagement()
            .delete(new BranchCleanup())
            .notificationForCommitters());
    return plan;
  }

  public PlanPermissions planPermission() {
    return new PlanPermissions(new PlanIdentifier("MS", "EB"))
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