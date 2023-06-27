package org.opendatamesh.platform.core.dpds.parser;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;

public class GitLocation extends UriLocation {

    public GitLocation(String repoUri, URI descriptorUri) {
        try {

            File sshDir = new File(FS.DETECTED.userHome(), ".ssh");
		    SshdSessionFactory sshdSessionFactory = new SshdSessionFactoryBuilder()
				.setPreferredAuthentications("publickey,keyboard-interactive,password")
				.setHomeDirectory(FS.DETECTED.userHome())
				.setSshDirectory(sshDir).build(new JGitKeyCache());
		    SshSessionFactory.setInstance(sshdSessionFactory);

            String repoName = repoUri.substring(repoUri.lastIndexOf('/') + 1);
            File localRepoPath = File.createTempFile("repoName", "");
            if(!localRepoPath.delete()) {
                throw new IOException("Could not delete temporary file " + localRepoPath);
            }

            CloneCommand clone = Git.cloneRepository()
                .setURI(repoUri.toString())
                .setDirectory(localRepoPath);
            
            Git result = clone.call();

            URI localRepoUri = localRepoPath.getAbsoluteFile().toURI();
            URI loacalDescriptorUri = localRepoUri.resolve(descriptorUri);
            setDescriptorUri(loacalDescriptorUri);
        } catch(IOException | GitAPIException e) {
            throw new RuntimeException("Impossible to create location", e);
        }
       
    }
    
}
