package foro.Unamba_forum.Business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import foro.Unamba_forum.Dto.DtoTotals;
import foro.Unamba_forum.Repository.RepoCareer;
import foro.Unamba_forum.Repository.RepoPublication;
import foro.Unamba_forum.Repository.RepoUser;

@Service
public class BusinessTotals {
    
    @Autowired
    private RepoCareer repoCareer;

    @Autowired
    private RepoUser repoUser;

    @Autowired
    private RepoPublication repoPublication;

    public DtoTotals getTotals() {
        DtoTotals totals = new DtoTotals();
        totals.setTotalCareers(repoCareer.count());
        totals.setTotalUsers(repoUser.count());
        totals.setTotalPublications(repoPublication.count());
        return totals;
    }
}
