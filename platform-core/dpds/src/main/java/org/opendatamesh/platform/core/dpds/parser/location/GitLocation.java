package org.opendatamesh.platform.core.dpds.parser.location;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.util.FS;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;

public class GitLocation extends UriLocation {

    String repoUri; 
    URI descriptorUri;   

    public GitLocation(String repoUri, URI descriptorUri) {
        this.repoUri = repoUri;
        this.descriptorUri = descriptorUri;
        this.opened = false;
    }

     @Override
    public void open() throws FetchException {
        if(opened == true) return;
        try {
            File sshDir = new File(FS.DETECTED.userHome(), ".ssh");
		    SshdSessionFactory sshdSessionFactory = new SshdSessionFactoryBuilder()
				.setPreferredAuthentications("publickey,keyboard-interactive,password")
				.setHomeDirectory(FS.DETECTED.userHome())
				.setSshDirectory(sshDir).build(new JGitKeyCache());
		    SshSessionFactory.setInstance(sshdSessionFactory);

            String repoName = repoUri.substring(repoUri.lastIndexOf('/') + 1);
            File localRepoDirectory = File.createTempFile(repoName, "");
            if(!localRepoDirectory.delete()) {
                throw new IOException("Could not delete temporary file " + localRepoDirectory);
            }

            CloneCommand clone = Git.cloneRepository()
                .setURI(repoUri.toString())
                .setDirectory(localRepoDirectory);
            
            Git result = clone.call();

            URI localRepoUri = localRepoDirectory.getAbsoluteFile().toURI();
            URI loacalDescriptorUri = localRepoUri.resolve(descriptorUri);
            setDescriptorUri(loacalDescriptorUri);
            opened = true;
        } catch(IOException | GitAPIException e) {
            throw new RuntimeException("Impossible to create location", e);
        }
    }

    @Override
    public void close() throws FetchException {
        URI uri = getRootDocumentBaseUri();
        File localRepoDirectory = new File(uri);
        try {
            FileUtils.deleteDirectory(localRepoDirectory);
        } catch (IOException e) {
            throw new FetchException("Could not delete temporary folder", uri);
        }
        
        
        opened = false;
    }
    
}
