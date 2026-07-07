import jenkins.model.*
import hudson.security.*
def instance = Jenkins.get()
def user = instance.securityRealm.getUser("canisha13")
user.addProperty(HudsonPrivateSecurityRealm.Details.fromPlainPassword("Password123!"))
instance.save()
println("Password reset complete")
