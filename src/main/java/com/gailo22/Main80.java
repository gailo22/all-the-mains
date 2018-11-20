package com.gailo22;

import java.util.function.Function;

public class Main80 {
	
	public static void main(String[] args) {
        FaceRepo faceRepo = new FaceRepo();
        FaceService faceService = new FaceService(faceRepo);

        int score1 = faceService.calcScore(FaceType.TYPE1);
        int score2 = faceService.calcScore(FaceType.TYPE2);
        int score3 = faceService.calcScore(FaceType.TYPE3);

        System.out.println("score1: " + score1);
        System.out.println("score2: " + score2);
        System.out.println("score3: " + score3);
    }

}

//@Repository
class FaceRepo {
    public int getScore(Integer id) {
        return 100;
    }
}

enum FaceType {
    TYPE1(FaceService::calc1),
    TYPE2(FaceService::calc2),
    TYPE3(FaceService::calc3);

    public Function<FaceService, Integer> faceAlgo;

    FaceType(Function<FaceService, Integer> faceAlgo) {
        this.faceAlgo = faceAlgo;
    }
}

//@Service
class FaceService {

    //@Autowired
    private FaceRepo faceRepo;

    public FaceService(FaceRepo faceRepo) {
        this.faceRepo = faceRepo;
    }

    public int calc1() {
        return 1 * faceRepo.getScore(1);
    }

    public int calc2() {
        return 2 * 100;
    }

    public int calc3() {
        return 3;
    }

    public int calcScore(FaceType type) {
        return type.faceAlgo.apply(this);
    }
}

