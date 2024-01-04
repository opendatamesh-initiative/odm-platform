package org.opendatamesh.platform.core.dpds.parser.location;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.opendatamesh.platform.core.commons.git.GitConfigurer;
import org.opendatamesh.platform.core.commons.git.GitService;
import org.opendatamesh.platform.core.dpds.exceptions.FetchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class GitLocation extends UriLocation {

    String repoUri; 
    
    URI descriptorUri;

    String branch;

    String tag;

    GitService gitService;

    private static final Logger logger = LoggerFactory.getLogger(GitLocation.class);
    

    public GitLocation(String repoUri, URI descriptorUri) {
        this(repoUri, descriptorUri, null, null);
    }
    /**
     * 
     * @param repoUri the ssh uri of the git repository
     * @param descriptorUri the uri of the descriptor file. It's relative to the repo root folder.
     */
    public GitLocation(String repoUri, URI descriptorUri, String branch, String tag) {
        this.repoUri = repoUri;
        this.descriptorUri = descriptorUri;
        this.branch = branch;
        this.tag = tag;
        this.opened = false;
        this.gitService = GitConfigurer.configureGitClient(
                "GENERIC",
                null,
                null
        );
    }

    @Override
    public void open() throws FetchException {
        if(opened == true) return;
        try {
            /*
            File sshDir = new File(FS.DETECTED.userHome(), ".ssh");
		    SshdSessionFactory sshdSessionFactory = new SshdSessionFactoryBuilder()
				.setPreferredAuthentications("publickey,keyboard-interactive,password")
				.setHomeDirectory(FS.DETECTED.userHome())
				.setSshDirectory(sshDir).build(new JGitKeyCache());
		    SshSessionFactory.setInstance(sshdSessionFactory);*/

            String repoName = repoUri.substring(repoUri.lastIndexOf('/') + 1);
            File localRepoDirectory = File.createTempFile(repoName, "");
            if(!localRepoDirectory.delete()) {
                throw new IOException("Could not delete temporary file " + localRepoDirectory);
            }

            /*CloneCommand clone = Git.cloneRepository()
                .setURI(repoUri.toString())
                .setDirectory(localRepoDirectory);
            
            if(branch != null) {
                clone.setCloneAllBranches(true);
                clone.setBranchesToClone(Arrays.asList("refs/heads/" + branch));
                clone.setBranch("refs/heads/" + branch);
            }
           
            Git cloneResult = clone.call();*/
            Git cloneResult;
            if(branch != null) {
                cloneResult = gitService.cloneRepo(
                        repoUri.toString(),
                        localRepoDirectory.getPath(),
                        true,
                        Arrays.asList("refs/heads/" + branch),
                        "refs/heads/" + branch
                );
            } else {
                cloneResult = gitService.cloneRepo(
                        repoUri.toString(),
                        localRepoDirectory.getPath()
                );
            }
            logger.debug("Repo [" + repoName + "] cloned to local folder [" + localRepoDirectory + "]");
            
            List<Ref> call = cloneResult.branchList().call();
            for (Ref ref : call) {
                logger.debug("Branch: " + ref + " " + ref.getName() + " " + ref.getObjectId().getName());
            }

            if(tag != null) {
                cloneResult.checkout().setName("refs/tags/" + tag).call();
            }
           
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
