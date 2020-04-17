import org.jenkinsci.plugins.scriptsecurity.scripts.*
def scriptApproval = org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval.get()

String[] signs = [
    "method org.jenkinsci.plugins.workflow.steps.FlowInterruptedException getCauses",
    "method org.jenkinsci.plugins.workflow.support.steps.input.Rejection getUser"
    ]

for( String sign : signs ) {
    scriptApproval.approveSignature(sign)
}

scriptApproval.save()
toApprove = ScriptApproval.get().getPendingScripts().collect()
toApprove.each {pending -> ScriptApproval.get().approveScript(pending.getHash())}