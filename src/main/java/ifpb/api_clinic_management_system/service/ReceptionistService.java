package ifpb.api_clinic_management_system.service;

import ifpb.api_clinic_management_system.exception.BusinessException;
import ifpb.api_clinic_management_system.exception.EntityNotFoundException;
import ifpb.api_clinic_management_system.mapper.ReceptionistMapper;
import ifpb.api_clinic_management_system.model.dto.receptionist.ReceptionistRequestDTO;
import ifpb.api_clinic_management_system.model.dto.receptionist.ReceptionistResponseDTO;
import ifpb.api_clinic_management_system.model.dto.receptionist.ReceptionistUpdateDTO;
import ifpb.api_clinic_management_system.model.entity.Receptionist;
import ifpb.api_clinic_management_system.model.entity.User;
import ifpb.api_clinic_management_system.repository.ReceptionistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReceptionistService {

    private final ReceptionistRepository receptionistRepository;
    private final ReceptionistMapper receptionistMapper;
    private final UserService userService;

    @Transactional
    public ReceptionistResponseDTO createReceptionist(ReceptionistRequestDTO dto) {
        ensureUserIsAvailable(dto.userId());
        User user = userService.fetchUserById(dto.userId());
        Receptionist receptionist = receptionistMapper.toReceptionist(dto, user);
        log.info("Creating receptionist for user id: {}", dto.userId());
        return receptionistMapper.toReceptionistResponseDTO(receptionistRepository.save(receptionist));
    }

    public ReceptionistResponseDTO findReceptionistById(Long id) {
        log.debug("Fetching receptionist by id: {}", id);
        return receptionistMapper.toReceptionistResponseDTO(fetchReceptionistById(id));
    }

    public Page<ReceptionistResponseDTO> findAllReceptionists(Pageable pageable) {
        log.debug("Fetching all receptionists - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return receptionistRepository.findAll(pageable)
                .map(receptionistMapper::toReceptionistResponseDTO);
    }

    @Transactional
    public ReceptionistResponseDTO updateReceptionist(Long id, ReceptionistUpdateDTO dto) {
        ensureAtLeastOneFieldProvided(dto);
        Receptionist receptionist = fetchReceptionistById(id);
        applyUpdates(receptionist, dto);
        log.info("Updating receptionist id: {}", id);
        return receptionistMapper.toReceptionistResponseDTO(receptionist);
    }

    @Transactional
    public ReceptionistResponseDTO disableReceptionist(Long id) {
        Receptionist receptionist = fetchReceptionistById(id);
        receptionist.getUser().setEnabled(false);
        log.info("Disabling receptionist id: {}", id);
        return receptionistMapper.toReceptionistResponseDTO(receptionist);
    }

    protected Receptionist fetchReceptionistById(Long id) {
        return receptionistRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Receptionist not found for id: {}", id);
                    return new EntityNotFoundException("Receptionist not found");
                });
    }

    private void applyUpdates(Receptionist receptionist, ReceptionistUpdateDTO dto) {
        if (dto.name() != null) {
            receptionist.setName(dto.name());
        }
        if (dto.phone() != null) {
            receptionist.setPhone(dto.phone());
        }
    }

    private void ensureUserIsAvailable(Long userId) {
        if (receptionistRepository.existsByUserId(userId)) {
            throw new BusinessException("User already linked to a receptionist");
        }
    }

    private void ensureAtLeastOneFieldProvided(ReceptionistUpdateDTO dto) {
        if (dto.name() == null
                && dto.phone() == null) {
            throw new BusinessException("At least one field must be provided for update");
        }
    }
}
